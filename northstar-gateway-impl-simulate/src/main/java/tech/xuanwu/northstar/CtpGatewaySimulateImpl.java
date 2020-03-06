package tech.xuanwu.northstar;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.CommonConstant;
import tech.xuanwu.northstar.engine.FastEventEngine;
import tech.xuanwu.northstar.entity.AccountInfo;
import tech.xuanwu.northstar.entity.PositionInfo;
import tech.xuanwu.northstar.gateway.GatewayApi;
import tech.xuanwu.northstar.gateway.SimulatedGateway;
import xyz.redtorch.common.util.UUIDStringPoolUtils;
import xyz.redtorch.pb.CoreEnum.DirectionEnum;
import xyz.redtorch.pb.CoreEnum.HedgeFlagEnum;
import xyz.redtorch.pb.CoreEnum.OrderStatusEnum;
import xyz.redtorch.pb.CoreEnum.PositionDirectionEnum;
import xyz.redtorch.pb.CoreEnum.TradeTypeEnum;
import xyz.redtorch.pb.CoreField.AccountField;
import xyz.redtorch.pb.CoreField.CancelOrderReqField;
import xyz.redtorch.pb.CoreField.ContractField;
import xyz.redtorch.pb.CoreField.GatewayField;
import xyz.redtorch.pb.CoreField.GatewaySettingField;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;
import xyz.redtorch.pb.CoreField.TickField;
import xyz.redtorch.pb.CoreField.TradeField;

/**
 * 模拟网关接口实现，采用真实行情模拟成交
 * @author kevinhuangwl
 *
 */
@Slf4j
public class CtpGatewaySimulateImpl implements GatewayApi, SimulatedGateway{
	
	private GatewayApi realGatewayApi;
	
	private FastEventEngine feEngine;
	
	/*账户信息*/
	private AccountField.Builder accountFieldBuilder = AccountField.newBuilder();
	
	/*合约挂单， <合约代码， 挂单队列>*/
	private ConcurrentHashMap<String, ConcurrentLinkedQueue<OrderField.Builder>> contractOrderMap = new ConcurrentHashMap<>();
	/*挂单，<挂单ID，挂单>*/
	private ConcurrentHashMap<String, OrderField.Builder> orderMap = new ConcurrentHashMap<>();
	/*持仓队列*/
	private ConcurrentHashMap<String, ConcurrentLinkedQueue<PositionInfo>> positionMap = new ConcurrentHashMap<>();

	public CtpGatewaySimulateImpl(GatewayApi realGatewayApi, FastEventEngine feEngine) {
		log.info("启动模拟市场网关");
		
		this.realGatewayApi = realGatewayApi;
		this.feEngine = feEngine;
		
	}
	
	@Override
	public void initGatewayAccount(AccountInfo accountInfo) {
		accountFieldBuilder.setAccountId(accountInfo.getAccountId());
		accountFieldBuilder.setAvailable(accountInfo.getAvailable());
		accountFieldBuilder.setBalance(accountInfo.getBalance());
		accountFieldBuilder.setCloseProfit(accountInfo.getCloseProfit());
		accountFieldBuilder.setCode(accountInfo.getCode());
		accountFieldBuilder.setCommission(accountInfo.getCommission());
		accountFieldBuilder.setCurrency(accountInfo.getCurrency());
		accountFieldBuilder.setDeposit(accountInfo.getDeposit());
		accountFieldBuilder.setGatewayId(accountInfo.getGatewayId());
		accountFieldBuilder.setHolder(accountInfo.getHolder());
		accountFieldBuilder.setMargin(accountInfo.getMargin());
		accountFieldBuilder.setName(accountInfo.getName());
		accountFieldBuilder.setPositionProfit(accountInfo.getPositionProfit());
		accountFieldBuilder.setPreBalance(accountInfo.getPreBalance());
		accountFieldBuilder.setWithdraw(accountInfo.getWithdraw());		
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
		String gatewayId = realGatewayApi.getGatewayId();
		String orderId = gatewayId + "@Simulate_" + UUIDStringPoolUtils.getUUIDString();
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
		ob.setTotalVolume(submitOrder.getVolume());
		ob.setOffsetFlag(submitOrder.getOffsetFlag());
		ob.setOrderPriceType(submitOrder.getOrderPriceType());
		ob.setGtdDate(submitOrder.getGtdDate());
		ob.setMinVolume(submitOrder.getMinVolume());
		ob.setStopPrice(submitOrder.getStopPrice());
		ob.setOrderStatus(OrderStatusEnum.OS_Unknown);
		
		if(!contractOrderMap.containsKey(unifiedSymbol)) {
			contractOrderMap.putIfAbsent(unifiedSymbol, new ConcurrentLinkedQueue<OrderField.Builder>());
		}
		contractOrderMap.get(unifiedSymbol).add(ob);
		orderMap.put(originOrderId, ob);
		
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
		
		feEngine.emitOrder(ob.build());
		return orderId;
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
					
					log.info("撤单成功，订单号：{}", originOrderId);
					feEngine.emitOrder(ob.build());
					return true;
				}
			}
		}
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
				feEngine.emitTrade(tradeField);
				feEngine.emitOrder(orderBuilder.build());
				
				DirectionEnum direction = orderBuilder.getDirection();
				HedgeFlagEnum hedgeFlag = orderBuilder.getHedgeFlag();
				String accountId = accountFieldBuilder.getAccountId();
				String positionId = unifiedSymbol + "@" + direction.getValueDescriptor().getName() + "@" + hedgeFlag.getValueDescriptor().getName() + "@" + accountId;
				PositionInfo pb = new PositionInfo();
				pb.setPositionId(positionId);
				pb.setAccountId(orderBuilder.getAccountId());
				pb.setPositionDirection(orderBuilder.getDirection()==DirectionEnum.D_Buy?PositionDirectionEnum.PD_Long:PositionDirectionEnum.PD_Short);
				pb.setPosition(orderBuilder.getTotalVolume());
				pb.setTdPosition(orderBuilder.getTotalVolume());
				
//				pb.setContractValue(value);
//				pb.setExchangeMargin(value);
				
				itOrder.remove();
			}
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
		return realGatewayApi.getGatewayId();
	}

	@Override
	public String getGatewayName() {
		return realGatewayApi.getGatewayName();
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
