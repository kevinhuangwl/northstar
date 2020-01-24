package tech.xuanwu.northstar.constant;

public interface MessageType {
	//注册策略
	String REG_STRATEGY = "RegisterStrategy";
	//下单
	String PLACE_ORDER = "PlaceOrder";
	//撤单
	String CANCEL_ORDER = "CancelOrder";
	//TICK行情数据
	String MARKET_TICK_DATA = "TickData";
	//Bar数据
	String MARKET_BAR_DATA = "BarData";
}
