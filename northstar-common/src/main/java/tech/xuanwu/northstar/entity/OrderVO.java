package tech.xuanwu.northstar.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import tech.xuanwu.northstar.utils.CtpSymbolNameConverter;
import xyz.redtorch.pb.CoreEnum.DirectionEnum;
import xyz.redtorch.pb.CoreEnum.OffsetFlagEnum;
import xyz.redtorch.pb.CoreEnum.OrderStatusEnum;

@Getter
@Setter
public class OrderVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3768565221247076401L;

	private String accountId;
	
	private String orderId;
	
	private String name;
	
	private String action;
	
	private String orderPrice;
	
	private int totalOrderVolume;
	
	private int waitingOrderVolume;
	
	private int tradedOrderVolume;
	
	private int canceledOrderVolume;
	
	private OrderStatusEnum orderState;
	
	private String state;
	
	private String orderTime;
	
	private static transient Map<OrderStatusEnum,String> statusMap = new HashMap<>() {
		{
			put(OrderStatusEnum.OS_AllTraded, "全成");
			put(OrderStatusEnum.OS_Canceled, "已撤单");
			put(OrderStatusEnum.OS_NoTradeNotQueueing, "未挂");
			put(OrderStatusEnum.OS_NoTradeQueueing, "全挂");
			put(OrderStatusEnum.OS_NotTouched, "未触发");
			put(OrderStatusEnum.OS_PartTradedNotQueueing, "未全成");
			put(OrderStatusEnum.OS_PartTradedQueueing, "部分成");
			put(OrderStatusEnum.OS_Rejected, "已拒绝");
			put(OrderStatusEnum.OS_Touched, "已触发");
			put(OrderStatusEnum.OS_Unknown, "未知");
		}
	};
	
	public static OrderVO convertFrom(OrderInfo o) {
		
		OrderVO vo = new OrderVO();
		vo.accountId = o.accountId;
		vo.orderId = o.getOrderId();
		vo.name = CtpSymbolNameConverter.convert(o.getContract().getSymbol());
		vo.action = (o.getDirection() == DirectionEnum.D_Buy ? "买" : "卖") + (o.getOffsetFlag() == OffsetFlagEnum.OF_Open ? "开" : o.getOffsetFlag() == OffsetFlagEnum.OF_CloseToday ? "平今" : "平");
		vo.orderPrice = String.valueOf(o.getPrice());
		vo.totalOrderVolume = o.getTotalVolume();
		vo.tradedOrderVolume = o.getTradedVolume();
		vo.canceledOrderVolume = o.getOrderStatus() == OrderStatusEnum.OS_Canceled ? vo.totalOrderVolume - vo.tradedOrderVolume : 0;
		vo.waitingOrderVolume = vo.totalOrderVolume - vo.tradedOrderVolume - vo.canceledOrderVolume;
		vo.state = statusMap.get(o.getOrderStatus());
		vo.orderState = o.getOrderStatus();
		vo.orderTime = o.getOrderTime();
		return vo;
	}
}
