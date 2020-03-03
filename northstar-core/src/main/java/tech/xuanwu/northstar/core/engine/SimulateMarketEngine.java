package tech.xuanwu.northstar.core.engine;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.CommonConstant;
import tech.xuanwu.northstar.core.persistence.repo.AccountRepo;
import tech.xuanwu.northstar.engine.FastEventEngine;
import tech.xuanwu.northstar.engine.MarketEngine;
import tech.xuanwu.northstar.entity.AccountInfo;
import xyz.redtorch.common.util.UUIDStringPoolUtils;
import xyz.redtorch.pb.CoreEnum.CurrencyEnum;
import xyz.redtorch.pb.CoreEnum.DirectionEnum;
import xyz.redtorch.pb.CoreEnum.OrderStatusEnum;
import xyz.redtorch.pb.CoreEnum.TradeTypeEnum;
import xyz.redtorch.pb.CoreField.AccountField;
import xyz.redtorch.pb.CoreField.CancelOrderReqField;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.PositionField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;
import xyz.redtorch.pb.CoreField.TickField;
import xyz.redtorch.pb.CoreField.TradeField;

/**
 * 模拟市场引擎，用于撮合模拟交易
 * @author kevinhuangwl
 *
 */
@Slf4j
@Component
@ConditionalOnExpression("${ctp.realTrader}==false")
public class SimulateMarketEngine implements MarketEngine, InitializingBean{
	
	@Autowired
	private FastEventEngine feEngine;
	
	@Value("${ctp.gatewayID}")
	private String gatewayId;
	
	/*账户信息*/
	private AccountField.Builder accountFieldBuilder = AccountField.newBuilder();
	
	@Autowired
	private AccountRepo accountRepo;
	
	/*合约挂单， <合约代码， 挂单队列>*/
	private ConcurrentHashMap<String, ConcurrentLinkedQueue<OrderField.Builder>> contractOrderMap = new ConcurrentHashMap<>();
	/*挂单，<挂单ID，挂单>*/
	private ConcurrentHashMap<String, OrderField.Builder> orderMap = new ConcurrentHashMap<>();
	/*持仓队列*/
	private ConcurrentLinkedQueue<PositionField.Builder> positionQ = new ConcurrentLinkedQueue<>();
	
	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("启动模拟市场引擎");
		
		AccountInfo accountInfo = accountRepo.getLatestAccountInfoByName(gatewayId);
		if(accountInfo == null) {
			log.info("未有模拟账户记录，初始化模拟账户");
			String code = "SimulateAccount";
			double initMoney = 100000;
			accountFieldBuilder.setCode(code);
			accountFieldBuilder.setAccountId(code + "@" + gatewayId);
			accountFieldBuilder.setAvailable(initMoney);
			accountFieldBuilder.setBalance(initMoney);
			accountFieldBuilder.setCurrency(CurrencyEnum.CNY);
			accountFieldBuilder.setGatewayId(gatewayId);

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
	public void submitOrder(SubmitOrderReqField submitOrder) {
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
	}

	@Override
	public void cancelOrder(CancelOrderReqField cancelOrder) {
		String originOrderId = cancelOrder.getOriginOrderId();
		OrderField.Builder orderBuilder = orderMap.remove(originOrderId);
		if(orderBuilder == null) {
			return;
		}
		if(orderBuilder.getOrderStatus() == OrderStatusEnum.OS_AllTraded) {
			log.info("挂单已全部成交，合约：{}，订单号：{}", orderBuilder.getContract().getUnifiedSymbol(), originOrderId);
			return;
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
					break;
				}
			}
		}
	}

	@Override
	public void updateTick(TickField tick) {
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
	public void deposit(double money) {
		
	}

	@Override
	public void withdraw(double money) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void proceedDailySettlement() {
		orderMap.clear();
		contractOrderMap.clear();
	}

}
