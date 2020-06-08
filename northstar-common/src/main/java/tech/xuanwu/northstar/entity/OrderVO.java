package tech.xuanwu.northstar.entity;

import java.io.Serializable;

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

	private String name;
	
	private String action;
	
	private String orderPrice;
	
	private int totalOrderVolume;
	
	private int waitingOrderVolume;
	
	private int tradedOrderVolume;
	
	private int canceledOrderVolume;
	
	private String state;
	
	private String orderTime;
	
	public static OrderVO convertFrom(OrderInfo o) {
		OrderVO vo = new OrderVO();
		vo.name = CtpSymbolNameConverter.convert(o.getContract().getSymbol());
		vo.action = (o.getDirection() == DirectionEnum.D_Buy ? "买" : "卖") + (o.getOffsetFlag() == OffsetFlagEnum.OF_Open ? "开" : o.getOffsetFlag() == OffsetFlagEnum.OF_CloseToday ? "平今" : "平");
		vo.orderPrice = String.valueOf(o.getPrice());
		vo.totalOrderVolume = o.getTotalVolume();
		vo.tradedOrderVolume = o.getTradedVolume();
		vo.canceledOrderVolume = o.getOrderStatus() == OrderStatusEnum.OS_Canceled ? vo.totalOrderVolume - vo.tradedOrderVolume : 0;
		vo.waitingOrderVolume = o.getOrderStatus() == OrderStatusEnum.OS_Unknown ? vo.totalOrderVolume - vo.tradedOrderVolume : 0;
		vo.state = o.getOrderStatus() == OrderStatusEnum.OS_AllTraded ? "全成" 
				: o.getOrderStatus() == OrderStatusEnum.OS_Canceled ? "已撤"
				: o.getOrderStatus() == OrderStatusEnum.OS_Rejected ? "已拒绝" 
				: o.getOrderStatus() == OrderStatusEnum.OS_Unknown ? "未成交" : "未知";
		vo.orderTime = o.getOrderTime();
		return vo;
	}
}
