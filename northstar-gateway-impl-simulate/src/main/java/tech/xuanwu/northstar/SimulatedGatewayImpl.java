package tech.xuanwu.northstar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.CommonConstant;
import tech.xuanwu.northstar.constant.NoticeCode;
import tech.xuanwu.northstar.domain.GwPosition;
import tech.xuanwu.northstar.engine.FastEventEngine;
import tech.xuanwu.northstar.entity.AccountInfo;
import tech.xuanwu.northstar.entity.ContractInfo;
import tech.xuanwu.northstar.entity.NoticeInfo;
import tech.xuanwu.northstar.entity.PositionInfo;
import tech.xuanwu.northstar.gateway.GatewayApi;
import tech.xuanwu.northstar.gateway.SimulatedGateway;
import xyz.redtorch.common.util.UUIDStringPoolUtils;
import xyz.redtorch.pb.CoreEnum.CommonStatusEnum;
import xyz.redtorch.pb.CoreEnum.DirectionEnum;
import xyz.redtorch.pb.CoreEnum.HedgeFlagEnum;
import xyz.redtorch.pb.CoreEnum.OffsetFlagEnum;
import xyz.redtorch.pb.CoreEnum.OrderStatusEnum;
import xyz.redtorch.pb.CoreEnum.PositionDirectionEnum;
import xyz.redtorch.pb.CoreEnum.TradeTypeEnum;
import xyz.redtorch.pb.CoreField.CancelOrderReqField;
import xyz.redtorch.pb.CoreField.ContractField;
import xyz.redtorch.pb.CoreField.GatewayField;
import xyz.redtorch.pb.CoreField.GatewaySettingField;
import xyz.redtorch.pb.CoreField.NoticeField;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.PositionField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;
import xyz.redtorch.pb.CoreField.TickField;
import xyz.redtorch.pb.CoreField.TradeField;

/**
 * 模拟网关接口实现，采用真实行情模拟成交
 * @author kevinhuangwl
 *
 */
@Slf4j
public class SimulatedGatewayImpl implements GatewayApi, SimulatedGateway{
	
	private GatewayApi realGatewayApi;
	
	private FastEventEngine feEngine;
	
	private final double DEFAULT_FEE_TICK = 1;
	
	/*账户信息*/
	private AccountInfo accountInfo;
	
	/*合约挂单， <合约代码， 挂单队列>*/
	private ConcurrentHashMap<String, ConcurrentLinkedQueue<OrderField.Builder>> contractOrderMap = new ConcurrentHashMap<>();
	/*挂单，<挂单ID，挂单>*/
	private ConcurrentHashMap<String, OrderField.Builder> orderMap = new ConcurrentHashMap<>();
	/*成交记录*/
	private ConcurrentHashMap<String, TradeField> tradeMap = new ConcurrentHashMap<>();
	/*持仓记录*/
	private ConcurrentHashMap<String, GwPosition> positionMap = new ConcurrentHashMap<>();

	public SimulatedGatewayImpl(GatewayApi realGatewayApi, FastEventEngine feEngine, AccountInfo accountInfo, List<PositionInfo> positionList) {
		log.info("启动模拟市场网关");
		
		this.realGatewayApi = realGatewayApi;
		this.feEngine = feEngine;
		this.accountInfo = accountInfo;
		
		for(PositionInfo p : positionList) {
			positionMap.put(p.getPositionId(), new GwPosition(p));
		}
	}
	
	@Override
	public boolean subscribe(ContractField contract) {
		return realGatewayApi.subscribe(contract);
	}

	@Override
	public boolean unsubscribe(ContractField contract) {
		return realGatewayApi.unsubscribe(contract);
	}
	
	@Override
	public String submitOrder(SubmitOrderReqField submitOrder) {
		//锁定资金
		ContractField contract = submitOrder.getContract();
		double marginRatio = submitOrder.getDirection() == DirectionEnum.D_Buy ? contract.getLongMarginRatio() : contract.getShortMarginRatio();
		double frozenMoney = submitOrder.getPrice() * submitOrder.getVolume() * contract.getMultiplier() * marginRatio;
		double transactionFee = submitOrder.getVolume() * contract.getPriceTick() * contract.getMultiplier() * DEFAULT_FEE_TICK;
		
		boolean noEnoughMoney = accountInfo.getAvailable() < frozenMoney + transactionFee;
		boolean noEnoughVolume = false;
		for(GwPosition p : positionMap.values()) {
			PositionField pf = p.getPositionField();
			if(!StringUtils.equals(contract.getContractId(), pf.getContract().getContractId())) {
				//合约不一致
				continue;
			}
			if(submitOrder.getOffsetFlag()==OffsetFlagEnum.OF_Open || submitOrder.getOffsetFlag()==OffsetFlagEnum.OF_Unkonwn) {
				//不是平仓委托
				continue;
			}
			
			//方向要匹配
			if(pf.getPositionDirection()==PositionDirectionEnum.PD_Long && submitOrder.getDirection()==DirectionEnum.D_Sell 
					|| pf.getPositionDirection()==PositionDirectionEnum.PD_Short && submitOrder.getDirection()==DirectionEnum.D_Buy ) {
				noEnoughVolume = pf.getPosition() < submitOrder.getVolume();
				break;
			}
		}
		boolean illegalOrder = noEnoughMoney || noEnoughVolume;
		
		String gatewayId = realGatewayApi.getGatewayId();
		String orderId = gatewayId + CommonConstant.SIM_TAG + "_" + UUIDStringPoolUtils.getUUIDString();
		String unifiedSymbol = submitOrder.getContract().getUnifiedSymbol();
		String originOrderId = submitOrder.getOriginOrderId();
		OrderField.Builder ob = OrderField.newBuilder();
		ob.setOrderId(orderId);
		ob.setContract(submitOrder.getContract());
		ob.setPrice(submitOrder.getPrice());
		ob.setDirection(submitOrder.getDirection());
		ob.setOriginOrderId(originOrderId);
		ob.setGatewayId(submitOrder.getGatewayId());
		ob.setVolumeCondition(submitOrder.getVolumeCondition());
		ob.setTradingDay(realGatewayApi.getTradingDay());
		ob.setOrderDate(LocalDate.now().format(CommonConstant.D_FORMAT_INT_FORMATTER));
		ob.setOrderTime(LocalTime.now().format(CommonConstant.T_FORMAT_FORMATTER));
		ob.setAccountId(gatewayId);
		ob.setTotalVolume(submitOrder.getVolume());
		ob.setOffsetFlag(submitOrder.getOffsetFlag());
		ob.setOrderPriceType(submitOrder.getOrderPriceType());
		ob.setGtdDate(submitOrder.getGtdDate());
		ob.setMinVolume(submitOrder.getMinVolume());
		ob.setStopPrice(submitOrder.getStopPrice());
		ob.setSequenceNo("1");
		ob.setOrderStatus(illegalOrder ? OrderStatusEnum.OS_Rejected : OrderStatusEnum.OS_Unknown);
		ob.setStatusMsg(noEnoughMoney ? "资金不足" : noEnoughVolume ? "仓位不足" : "报单已提交");
		
		if(!contractOrderMap.containsKey(unifiedSymbol)) {
			contractOrderMap.putIfAbsent(unifiedSymbol, new ConcurrentLinkedQueue<OrderField.Builder>());
		}
		contractOrderMap.get(unifiedSymbol).add(ob);
		orderMap.put(originOrderId, ob);
		
		if(ob.getOrderStatus() == OrderStatusEnum.OS_Unknown) {			
			log.info("模拟交易接口发单记录->{\n" //
					+ "InstrumentID:{},\n" //
					+ "LimitPrice:{},\n" //
					+ "VolumeTotalOriginal:{},\n" //
					+ "OrderPriceType:{},\n" //
					+ "Direction:{},\n" //
					+ "CombOffsetFlag:{},\n" //
					+ "CombHedgeFlag:{},\n" //
					+ "ContingentCondition:{},\n" //
					+ "ForceCloseReason:{},\n" //
					+ "MinVolume:{},\n" //
					+ "TimeCondition:{},\n" //
					+ "VolumeCondition:{},\n" //
					+ "StopPrice:{}}", //
					submitOrder.getContract().getSymbol(), //
					submitOrder.getPrice(), //
					submitOrder.getVolume(), //
					submitOrder.getOrderPriceType(), //
					submitOrder.getDirection(), //
					submitOrder.getOffsetFlag(), //
					submitOrder.getHedgeFlag(), //
					submitOrder.getContingentCondition(), //
					submitOrder.getForceCloseReason(), //
					submitOrder.getMinVolume(), //
					submitOrder.getTimeCondition(), //
					submitOrder.getVolumeCondition(), //
					submitOrder.getStopPrice());
		}
		
		feEngine.emitOrder(ob.build());
		
		updateAccount();
		return orderId;
	}
	
	private void updateAccount() {
		synchronized (accountInfo) {
			double holdingProfit = 0;
			double commission = 0;
			double totalFrozen = 0;
			double totalMargin = 0;
			
			for(Entry<String, OrderField.Builder> e : orderMap.entrySet()) {
				OrderField.Builder ob = e.getValue();
				if(ob.getOrderStatus()==OrderStatusEnum.OS_AllTraded || ob.getOrderStatus()==OrderStatusEnum.OS_Rejected
						|| ob.getOrderStatus()==OrderStatusEnum.OS_Canceled) {
					continue;
				}
				ContractField contract = ob.getContract();
				double marginRatio = ob.getDirection() == DirectionEnum.D_Buy ? contract.getLongMarginRatio() : contract.getShortMarginRatio();
				double frozenMoney = ob.getOrderStatus() == OrderStatusEnum.OS_Unknown 
						? ob.getTotalVolume() * ob.getPrice() * contract.getMultiplier() * marginRatio
						: 0;
				totalFrozen += frozenMoney;
			}
			
			for(Entry<String, GwPosition> e : positionMap.entrySet()) {
				PositionInfo p = e.getValue().getPositionInfo();
				totalMargin += p.getUseMargin();
				holdingProfit += p.getPositionProfit();
			}
			
			for(Entry<String, TradeField> e : tradeMap.entrySet()) {
				TradeField t = e.getValue();
				commission += t.getContract().getPriceTick() * t.getContract().getMultiplier() * DEFAULT_FEE_TICK * t.getVolume();
			}
			
			accountInfo.setCommission(commission);
			accountInfo.setPositionProfit(holdingProfit);
			accountInfo.setBalance(accountInfo.getPreBalance() + holdingProfit - commission + accountInfo.getDeposit() - accountInfo.getWithdraw());
			accountInfo.setAvailable(accountInfo.getBalance() - totalFrozen - totalMargin);
			
		}
		
		feEngine.emitAccount(accountInfo.convertTo());
	}
	
	@Override
	public boolean cancelOrder(CancelOrderReqField cancelOrder) {
		String originOrderId = cancelOrder.getOriginOrderId();
		OrderField.Builder orderBuilder = orderMap.remove(originOrderId);
		if(orderBuilder == null) {
			return false;
		}
		if(orderBuilder.getOrderStatus() == OrderStatusEnum.OS_AllTraded) {
			log.info("挂单已全部成交，合约：{}，订单号：{}", orderBuilder.getContract().getUnifiedSymbol(), originOrderId);
			return false;
		}
		if(orderBuilder.getOrderStatus() == OrderStatusEnum.OS_Unknown) {
			String unifiedSymbol = orderBuilder.getContract().getUnifiedSymbol();
			log.info("模拟撤单，合约：{}，订单号：{}", unifiedSymbol, originOrderId);
			
			ConcurrentLinkedQueue<OrderField.Builder> orderWaitingQ = contractOrderMap.get(unifiedSymbol);
			Iterator<OrderField.Builder> itOrder = orderWaitingQ.iterator();
			while(itOrder.hasNext()) {
				OrderField.Builder ob = itOrder.next();
				if(StringUtils.equals(ob.getOriginOrderId(), originOrderId)) {
					itOrder.remove();
					ob.setOrderStatus(OrderStatusEnum.OS_Canceled);
					ob.setSequenceNo(String.valueOf(Integer.valueOf(ob.getSequenceNo()) + 1));
					ob.setOrderDate(LocalDate.now().format(CommonConstant.D_FORMAT_INT_FORMATTER));
					ob.setOrderTime(LocalTime.now().format(CommonConstant.T_FORMAT_FORMATTER));
					ob.setStatusMsg("全部撤单");
					
					log.info("撤单成功，订单号：{}", originOrderId);
					feEngine.emitOrder(ob.build());
					return true;
				}
			}
		}
		updateAccount();
		return false;
	}
	
	@Override
	public void emitTick(TickField tick) {
		String unifiedSymbol = tick.getUnifiedSymbol();
		ConcurrentLinkedQueue<OrderField.Builder> orderWaitingQ = contractOrderMap.get(unifiedSymbol);
		if(orderWaitingQ == null || orderWaitingQ.size() == 0) {
			return;
		}
		Iterator<OrderField.Builder> itOrder = orderWaitingQ.iterator();
		while(itOrder.hasNext()) {
			OrderField.Builder orderBuilder = itOrder.next();
			
			TradeField tradeField = deal(orderBuilder, tick);
			if(tradeField != null) {				
				tradeMap.put(tradeField.getTradeId(), tradeField);
				feEngine.emitTrade(tradeField);
				feEngine.emitOrder(orderBuilder.build());
				
				//成交后构建合约持仓
				DirectionEnum direction = orderBuilder.getDirection();
				HedgeFlagEnum hedgeFlag = orderBuilder.getHedgeFlag();
				String accountId = accountInfo.getAccountId();
				String positionId = unifiedSymbol + "@" + direction.getValueDescriptor().getName() + "@" + hedgeFlag.getValueDescriptor().getName() + "@" + accountId;
				
				boolean isLongPosition = orderBuilder.getDirection()==DirectionEnum.D_Buy;
				boolean isOpen = orderBuilder.getOffsetFlag() == OffsetFlagEnum.OF_Open;
				GwPosition gp;
				if(positionMap.containsKey(positionId)) {
					gp = positionMap.get(positionId);
				}else {
					PositionInfo pi = new PositionInfo();
					pi.setPositionId(positionId);
					pi.setAccountId(orderBuilder.getAccountId());
					pi.setPositionDirection(isLongPosition ? PositionDirectionEnum.PD_Long : PositionDirectionEnum.PD_Short);
					pi.setContract(ContractInfo.convertFrom(tradeField.getContract()));
					gp = new GwPosition(pi);
				}
				
				if(isOpen) {
					gp.addPosition(tradeField);
				}else {
					gp.reducePosition(tradeField);
				}
				
				positionMap.put(positionId, gp);
				itOrder.remove();
				
				updateAccount();
			}
		}
		
		updatePosition(tick);
	}
	
	private void updatePosition(TickField tick) {
		for(GwPosition p : positionMap.values()) {
			if(!StringUtils.equals(p.getPositionInfo().getContract().getUnifiedSymbol(), tick.getUnifiedSymbol())) {
				continue;
			}
			
			p.updateByTick(tick);
		}
	}
	
	private TradeField deal(OrderField.Builder order, TickField tick) {
		//判断是否为同一品种
		if(StringUtils.equals(order.getContract().getUnifiedSymbol(), tick.getUnifiedSymbol())) {
			return null;
		}
		//判断是否达到成交条件
		boolean buyConditionSatisfied = order.getPrice() >= tick.getAskPrice(0) && order.getDirection() == DirectionEnum.D_Buy;
		boolean sellConditionSatisfied = order.getPrice() <= tick.getBidPrice(0) && order.getDirection() == DirectionEnum.D_Sell;
		if(!buyConditionSatisfied && !sellConditionSatisfied) {
			return null;
		}
		
		TradeField.Builder tb = TradeField.newBuilder();
		tb.setTradeId(UUIDStringPoolUtils.getUUIDString());
		tb.setAccountId(order.getAccountId());
		tb.setContract(order.getContract());
		tb.setDirection(order.getDirection());
		tb.setGatewayId(order.getGatewayId());
		tb.setHedgeFlag(order.getHedgeFlag());
		tb.setOffsetFlag(order.getOffsetFlag());
		tb.setOrderId(order.getOrderId());
		LocalDate now = LocalDate.now();
		tb.setTradeDate(now.format(CommonConstant.D_FORMAT_INT_FORMATTER));
		tb.setTradeTime(now.format(CommonConstant.T_FORMAT_WITH_MS_INT_FORMATTER));
		tb.setTradeTimestamp(System.currentTimeMillis());
		tb.setTradeType(TradeTypeEnum.TT_Common);
		tb.setPrice(buyConditionSatisfied ? tick.getAskPrice(0) : tick.getBidPrice(0));
		tb.setVolume(buyConditionSatisfied ? tick.getAskVolume(0) : tick.getBidVolume(0));
		
		order.setTradedVolume(order.getTotalVolume());
		order.setOrderStatus(OrderStatusEnum.OS_AllTraded);
		
		log.info("模拟{}单全部成交，合约：{}", buyConditionSatisfied?"多":"空", tick.getUnifiedSymbol());
		
		return tb.build();
	}

	@Override
	public void connect() {
		realGatewayApi.connect();
		
		while(!realGatewayApi.isConnected());
		
		feEngine.emitAccount(accountInfo.convertTo());
		for(GwPosition p : positionMap.values()) {
			feEngine.emitPosition(p.getPositionField());
		}
		
		NoticeInfo noticeInfo = new NoticeInfo();
		noticeInfo.setEvent(NoticeCode.GATEWAY_READY);
		noticeInfo.setMessage("网关:" + getGatewayName() + ",网关ID:" + getGatewayId() + "可以交易");
		noticeInfo.setData(getGatewayId());
		
		NoticeField.Builder noticeBuilder = NoticeField.newBuilder();
		noticeBuilder.setContent(new Gson().toJson(noticeInfo));
		noticeBuilder.setStatus(CommonStatusEnum.COMS_SUCCESS);
		noticeBuilder.setTimestamp(System.currentTimeMillis());
		feEngine.emitNotice(noticeBuilder.build());
	}

	@Override
	public void disconnect() {
		realGatewayApi.disconnect();		
	}

	@Override
	public boolean isConnected() {
		return realGatewayApi.isConnected();
	}

	@Override
	public String getGatewayId() {
		return realGatewayApi.getGatewayId() + CommonConstant.SIM_TAG;
	}

	@Override
	public String getGatewayName() {
		return realGatewayApi.getGatewayName() + CommonConstant.SIM_TAG;
	}

	@Override
	public GatewayField getGateway() {
		return realGatewayApi.getGateway();
	}

	@Override
	public String getTradingDay() {
		return realGatewayApi.getTradingDay();
	}

	@Override
	public GatewaySettingField getGatewaySetting() {
		return realGatewayApi.getGatewaySetting();
	}

	@Override
	public boolean getAuthErrorFlag() {
		return realGatewayApi.getAuthErrorFlag();
	}

	@Override
	public void setAuthErrorFlag(boolean loginErrorFlag) {
		realGatewayApi.setAuthErrorFlag(loginErrorFlag);
	}

	@Override
	public long getLastConnectBeginTimestamp() {
		return realGatewayApi.getLastConnectBeginTimestamp();
	}

	@Override
	public FastEventEngine getEventEngine() {
		return feEngine;
	}

	@Override
	public void proceedDailySettlement() {
		// TODO Auto-generated method stub
		
	}

}
