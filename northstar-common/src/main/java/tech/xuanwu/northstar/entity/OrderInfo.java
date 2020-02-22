package tech.xuanwu.northstar.entity;

import java.io.Serializable;

import lombok.Data;
import xyz.redtorch.pb.CoreEnum.DirectionEnum;
import xyz.redtorch.pb.CoreEnum.OffsetFlagEnum;
import xyz.redtorch.pb.CoreEnum.OrderStatusEnum;
import xyz.redtorch.pb.CoreField.OrderField;

@Data
public class OrderInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2821469480598527381L;
	
	String originOrderId;
	String orderId;  // 定单ID,通常是<网关ID@适配器定单ID>
	String accountId;  // 账户ID
	DirectionEnum direction;  // 方向
	OffsetFlagEnum offsetFlag;  // 开平
	OrderStatusEnum orderStatus;  // 状态
	double price;  // 价格
	int totalVolume;  // 数量
	int tradedVolume;  // 已成交数量
	String gtdDate; // GTD日期
	int minVolume; // 最小成交量
	double stopPrice; // 止损价
	String tradingDay;  // 交易日
	String orderDate;  // 定单日期
	String orderTime;  // 定单时间
	String activeTime;  // 激活时间
	String suspendTime;  // 挂起时间
	String cancelTime;  // 撤销时间
	String updateTime;  // 最后修改时间
	String statusMsg;  // 状态信息
    String contractSymbol;  // 合约
	String gatewayId;  // 网关ID
	
	
	public static OrderInfo convertFrom(OrderField orderField) {
		OrderInfo info = new OrderInfo();
		info.accountId = orderField.getAccountId();
		info.activeTime = orderField.getActiveTime();
		info.cancelTime = orderField.getCancelTime();
		info.contractSymbol = orderField.getContract().getSymbol();
		info.direction = orderField.getDirection();
		info.gatewayId = orderField.getGatewayId();
		info.gtdDate = orderField.getGtdDate();
		info.minVolume = orderField.getMinVolume();
		info.offsetFlag = orderField.getOffsetFlag();
		info.orderDate = orderField.getOrderDate();
		info.orderId = orderField.getOrderId();
		info.orderStatus = orderField.getOrderStatus();
		info.orderTime = orderField.getOrderTime();
		info.originOrderId = orderField.getOriginOrderId();
		info.price = orderField.getPrice();
		info.statusMsg = orderField.getStatusMsg();
		info.stopPrice = orderField.getStopPrice();
		info.suspendTime = orderField.getSuspendTime();
		info.totalVolume = orderField.getTotalVolume();
		info.tradedVolume = orderField.getTradedVolume();
		info.tradingDay = orderField.getTradingDay();
		info.updateTime = orderField.getUpdateTime();
		return info;
	}
}
