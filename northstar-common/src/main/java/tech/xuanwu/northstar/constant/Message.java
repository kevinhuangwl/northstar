package tech.xuanwu.northstar.constant;

public interface Message {
	//注册策略
	String REG_STRATEGY = "RegisterStrategy";
	//注销策略
	String UNREG_STRATEGY = "UnregisterStrategy";
	//下单
	String SUBMIT_ORDER = "SubmitOrder";
	//撤单
	String CANCEL_ORDER = "CancelOrder";
	//TICK行情数据
	String MARKET_TICK_DATA = "TickData";
	//Bar数据
	String MARKET_BAR_DATA = "BarData";
}
