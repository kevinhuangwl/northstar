package tech.xuanwu.northstar.core.engine.rt.event;

import java.time.LocalDate;
import java.util.EventObject;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.CommonConstant;
import tech.xuanwu.northstar.constant.RuntimeEvent;
import tech.xuanwu.northstar.core.config.props.CtpGatewaySettings;
import tech.xuanwu.northstar.core.persistence.repo.AccountRepo;
import tech.xuanwu.northstar.engine.FastEventEngine;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.entity.AccountInfo;
import xyz.redtorch.pb.CoreEnum.CurrencyEnum;
import xyz.redtorch.pb.CoreEnum.DirectionEnum;
import xyz.redtorch.pb.CoreEnum.OrderStatusEnum;
import xyz.redtorch.pb.CoreEnum.TradeTypeEnum;
import xyz.redtorch.pb.CoreField.AccountField;
import xyz.redtorch.pb.CoreField.CancelOrderReqField;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;
import xyz.redtorch.pb.CoreField.TickField;
import xyz.redtorch.pb.CoreField.TradeField;

/**
 * 模拟市场事件处理器，用于撮合模拟交易
 * @author kevinhuangwl
 *
 */
@Slf4j
@Component
@ConditionalOnExpression("${ctp.realTrader}==false")
public class SimulateMarketEventHandler implements RuntimeEngine.Listener, InitializingBean{

	@Autowired
	private RuntimeEngine rtEngine;
	
	@Autowired
	private FastEventEngine feEngine;
	
	@Autowired
	private CtpGatewaySettings ctpProp;
	
	private AccountField.Builder accountFieldBuilder = AccountField.newBuilder();
	
	private ConcurrentLinkedQueue<TradeField.Builder> transBuilderQ = new ConcurrentLinkedQueue<>();
	
	@Autowired
	private AccountRepo accountRepo;
	
	private ConcurrentHashMap<String, ConcurrentLinkedQueue<OrderField.Builder>> contractOrderMap = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, String> orderContractMap = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, OrderField.Builder> tradedOrderMap = new ConcurrentHashMap<>();

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("启动模拟市场");
		rtEngine.addEventHandler(RuntimeEvent.TICK_UPDATE, this);	
		rtEngine.addEventHandler(RuntimeEvent.SUBMIT_ORDER_SIMULATE, this);
		rtEngine.addEventHandler(RuntimeEvent.CANCEL_ORDER_SIMULATE, this);
		
		AccountInfo accountInfo = accountRepo.getLatestAccountInfoByName(ctpProp.getGatewayID());
		if(accountInfo == null) {
			log.info("未有模拟账户记录，初始化模拟账户");
			String code = "SimulateAccount";
			double initMoney = 100000;
			accountFieldBuilder.setCode(code);
			accountFieldBuilder.setAccountId(code + "@" + ctpProp.getGatewayID());
			accountFieldBuilder.setAvailable(initMoney);
			accountFieldBuilder.setBalance(initMoney);
			accountFieldBuilder.setCurrency(CurrencyEnum.CNY);
			accountFieldBuilder.setGatewayId(ctpProp.getGatewayID());

		}else {
			log.info("已有模拟账户记录，读取账户信息");
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
		
		feEngine.emitAccount(accountFieldBuilder.build());
	}

	@Override
	public void onEvent(EventObject e) throws Exception {
		Object obj = e.getSource();
		if(obj instanceof TickField) {
			TickField tick = (TickField) obj;
			onTick(tick);
			doRealTimeCalculation();
		}else if (obj instanceof SubmitOrderReqField) {
			onSubmitOrder((SubmitOrderReqField) obj);
			
		}else if (obj instanceof CancelOrderReqField) {
			onCancelOrder((CancelOrderReqField) obj);
			
		}
	}
	
	
	private void doRealTimeCalculation() {
		
	}

	private void onTick(TickField tick) {
		String unifiedSymbol = tick.getUnifiedSymbol();
		ConcurrentLinkedQueue<OrderField.Builder> orderWaitingQ = contractOrderMap.get(unifiedSymbol);
		if(orderWaitingQ == null || orderWaitingQ.size() == 0) {
			return;
		}
		Iterator<OrderField.Builder> itOrder = orderWaitingQ.iterator();
		while(itOrder.hasNext()) {
			OrderField.Builder ob = itOrder.next();
			
			if(ob.getDirection() == DirectionEnum.D_Buy && ob.getPrice() >= tick.getAskPrice(0)) {
				TradeField.Builder tb = TradeField.newBuilder();
				tb.setAccountId(ob.getAccountId());
				tb.setContract(ob.getContract());
				tb.setDirection(ob.getDirection());
				tb.setGatewayId(ob.getGatewayId());
				tb.setHedgeFlag(ob.getHedgeFlag());
				tb.setOffsetFlag(ob.getOffsetFlag());
				tb.setOrderId(ob.getOrderId());
				LocalDate now = LocalDate.now();
				tb.setTradeDate(now.format(CommonConstant.D_FORMAT_INT_FORMATTER));
				tb.setTradeTime(now.format(CommonConstant.T_FORMAT_WITH_MS_INT_FORMATTER));
				tb.setTradeTimestamp(System.currentTimeMillis());
				tb.setTradeType(TradeTypeEnum.TT_Common);
				
				//达到多单成交条件
				if(tick.getAskVolume(0) >= ob.getTotalVolume() - ob.getTradedVolume()) {
					
					log.info("模拟多单全部成交，合约：{}", unifiedSymbol);
					
					tb.setPrice(tick.getAskPrice(0));
					tb.setVolume(tick.getAskVolume(0));
					
					//全部成交
					ob.setTradedVolume(ob.getTotalVolume());
					ob.setOrderStatus(OrderStatusEnum.OS_AllTraded);
					
					itOrder.remove();
					orderContractMap.remove(ob.getOriginOrderId());
					tradedOrderMap.put(unifiedSymbol, ob);
					
					feEngine.emitOrder(ob.build());
				}else {
					
					log.info("模拟多单部分成交，合约：{}", unifiedSymbol);
					
					tb.setPrice(tick.getBidPrice(0));
					tb.setVolume(tick.getBidVolume(0));
					
					//部份成交
					ob.setTradedVolume(tick.getAskVolume(0) + ob.getTradedVolume());
					ob.setOrderStatus(OrderStatusEnum.OS_PartTradedQueueing);
					
				}
				
				feEngine.emitTrade(tb.build());
				
			}else if(ob.getDirection() == DirectionEnum.D_Sell && ob.getPrice() <= tick.getBidPrice(0)) {
				TradeField.Builder tb = TradeField.newBuilder();
				tb.setAccountId(ob.getAccountId());
				tb.setContract(ob.getContract());
				tb.setDirection(ob.getDirection());
				tb.setGatewayId(ob.getGatewayId());
				tb.setHedgeFlag(ob.getHedgeFlag());
				tb.setOffsetFlag(ob.getOffsetFlag());
				tb.setOrderId(ob.getOrderId());
				LocalDate now = LocalDate.now();
				tb.setTradeDate(now.format(CommonConstant.D_FORMAT_INT_FORMATTER));
				tb.setTradeTime(now.format(CommonConstant.T_FORMAT_WITH_MS_INT_FORMATTER));
				tb.setTradeTimestamp(System.currentTimeMillis());
				tb.setTradeType(TradeTypeEnum.TT_Common);
				
				//达到空单成交条件
				if(tick.getBidVolume(0) >= ob.getTotalVolume() - ob.getTradedVolume()) {
					
					log.info("模拟空单全部成交，合约：{}", unifiedSymbol);
					
					tb.setPrice(tick.getBidPrice(0));
					tb.setVolume(tick.getBidVolume(0));
					
					//全部成交
					ob.setTradedVolume(ob.getTotalVolume());
					ob.setOrderStatus(OrderStatusEnum.OS_AllTraded);
					
					itOrder.remove();
					orderContractMap.remove(ob.getOriginOrderId());
					tradedOrderMap.put(unifiedSymbol, ob);
					
					feEngine.emitOrder(ob.build());
				} else {
					
					log.info("模拟空单部分成交，合约：{}", unifiedSymbol);
					
					tb.setPrice(tick.getAskPrice(0));
					tb.setVolume(tick.getAskVolume(0));
					
					//部份成交
					ob.setTradedVolume(tick.getBidVolume(0) + ob.getTradedVolume());
					ob.setOrderStatus(OrderStatusEnum.OS_PartTradedQueueing);
				}
				
				feEngine.emitTrade(tb.build());
			}
		}
	}
	
	private void onSubmitOrder(SubmitOrderReqField submitOrder) {
		String unifiedSymbol = submitOrder.getContract().getUnifiedSymbol();
		String originOrderId = submitOrder.getOriginOrderId();
		OrderField.Builder ob = OrderField.newBuilder();
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
		orderContractMap.put(originOrderId, unifiedSymbol);
		
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
	}
	
	private void onCancelOrder(CancelOrderReqField cancelOrder) {
		String originOrderId = cancelOrder.getOriginOrderId();
		String unifiedSymbol = orderContractMap.remove(originOrderId);
		
		log.info("模拟撤单，合约：{}，订单号：{}", unifiedSymbol, originOrderId);
		
		if(tradedOrderMap.containsKey(originOrderId)) {
			log.info("委托已失效，订单号：{}", originOrderId);
			feEngine.emitOrder(tradedOrderMap.get(originOrderId).build());
			return;
		}
		
		ConcurrentLinkedQueue<OrderField.Builder> orderWaitingQ = contractOrderMap.get(unifiedSymbol);
		Iterator<OrderField.Builder> itOrder = orderWaitingQ.iterator();
		while(itOrder.hasNext()) {
			OrderField.Builder ob = itOrder.next();
			if(StringUtils.equals(ob.getOriginOrderId(), originOrderId)) {
				itOrder.remove();
				ob.setOrderStatus(OrderStatusEnum.OS_Canceled);
				tradedOrderMap.put(originOrderId, ob);
				
				log.info("撤单成功，订单号：{}", originOrderId);
				feEngine.emitOrder(ob.build());
				break;
			}
		}
	}
	
}
