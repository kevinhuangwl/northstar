package tech.xuanwu.northstar;

import java.time.LocalDate;
import java.time.LocalTime;

import tech.xuanwu.northstar.constant.CommonConstant;
import tech.xuanwu.northstar.gateway.GatewayApi;
import xyz.redtorch.common.util.UUIDStringPoolUtils;
import xyz.redtorch.pb.CoreEnum.DirectionEnum;
import xyz.redtorch.pb.CoreEnum.OffsetFlagEnum;
import xyz.redtorch.pb.CoreEnum.OrderStatusEnum;
import xyz.redtorch.pb.CoreEnum.TradeTypeEnum;
import xyz.redtorch.pb.CoreField.ContractField;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;
import xyz.redtorch.pb.CoreField.TickField;
import xyz.redtorch.pb.CoreField.TradeField;

/**
 * 委托单领域对象，负责委托单的状态转换
 * @author kevinhuangwl
 *
 */
class GwOrder {

	private OrderField.Builder ob;
	
	private GatewayApi gateway;
	
	public GwOrder(GatewayApi gateway){
		this.gateway = gateway;
	}
	
	public String getOriginOrderId() {
		return ob.getOriginOrderId();
	}
	
	public ContractField getContract() {
		return ob.getContract();
	}
	
	public TradeField tryDeal(TickField tick) {
		//判断是否达到成交条件
		boolean buyConditionSatisfied = ob.getPrice() >= tick.getAskPrice(0) && ob.getDirection() == DirectionEnum.D_Buy;
		boolean sellConditionSatisfied = ob.getPrice() <= tick.getBidPrice(0) && ob.getDirection() == DirectionEnum.D_Sell;
		if(!buyConditionSatisfied && !sellConditionSatisfied) {
			return null;
		}
		
		TradeField.Builder tb = TradeField.newBuilder();
		tb.setTradeId(UUIDStringPoolUtils.getUUIDString());
		tb.setAccountId(ob.getAccountId());
		tb.setContract(ob.getContract());
		tb.setDirection(ob.getDirection());
		tb.setGatewayId(ob.getGatewayId());
		tb.setHedgeFlag(ob.getHedgeFlag());
		tb.setOffsetFlag(ob.getOffsetFlag());
		tb.setOrderId(ob.getOrderId());
		tb.setTradeDate(LocalDate.now().format(CommonConstant.D_FORMAT_INT_FORMATTER));
		tb.setTradeTime(LocalTime.now().format(CommonConstant.T_FORMAT_FORMATTER));
		tb.setTradeTimestamp(System.currentTimeMillis());
		tb.setTradeType(TradeTypeEnum.TT_Common);
		tb.setPrice(buyConditionSatisfied ? tick.getAskPrice(0) : tick.getBidPrice(0));
		tb.setVolume(ob.getTotalVolume());
		
		return tb.build();
	}
	
	public OrderField initFrom(SubmitOrderReqField submitOrder) {
		String gatewayId = gateway.getGatewayId();
		String orderId = gatewayId + CommonConstant.SIM_TAG + "_" + UUIDStringPoolUtils.getUUIDString();
		String originOrderId = submitOrder.getOriginOrderId();
		OrderField.Builder ob = OrderField.newBuilder();
		ob.setOrderId(orderId);
		ob.setContract(submitOrder.getContract());
		ob.setPrice(submitOrder.getPrice());
		ob.setDirection(submitOrder.getDirection());
		ob.setOriginOrderId(originOrderId);
		ob.setGatewayId(submitOrder.getGatewayId());
		ob.setVolumeCondition(submitOrder.getVolumeCondition());
		ob.setTradingDay(gateway.getTradingDay());
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
		
		this.ob = ob;
		return ob.build();
	}
	
	public OrderField orderConfirmed() {
		ob.setOrderStatus(OrderStatusEnum.OS_Unknown);
		ob.setStatusMsg("报单已提交");
		return ob.build();
	}
	
	public OrderField orderRejected() {
		ob.setOrderStatus(OrderStatusEnum.OS_Rejected);
		ob.setStatusMsg(ob.getOffsetFlag() == OffsetFlagEnum.OF_Open ? "资金不足" : "仓位不足");
		return ob.build();
	}
	
	public OrderField orderCancelled() {
		ob.setOrderStatus(OrderStatusEnum.OS_Canceled);
		ob.setSequenceNo(String.valueOf(Integer.valueOf(ob.getSequenceNo()) + 1));
		ob.setOrderDate(LocalDate.now().format(CommonConstant.D_FORMAT_INT_FORMATTER));
		ob.setOrderTime(LocalTime.now().format(CommonConstant.T_FORMAT_FORMATTER));
		ob.setStatusMsg("全部撤单成功");
		return ob.build();
	}
	
	public OrderField orderTraded() {
		ob.setTradedVolume(ob.getTotalVolume());
		ob.setOrderStatus(OrderStatusEnum.OS_AllTraded);
		ob.setStatusMsg("报单全部已成交");
		return ob.build();
	}
	
	
}
