package tech.xuanwu.northstar.constant;

/**
 * 运行时引擎事件枚举
 * @author kevinhuangwl
 *
 */
public enum RuntimeEvent {
	
	//更新TICK数据
	TICK_UPDATE,
	//注册策略
	REGISTER_STRATEGY,
	//下单
	SUBMIT_ORDER,
	//撤单
	CANCEL_ORDER,
	//订单回报
	FEEDBACK_ORDER,
	//账户回报
	FEEDBACK_ACCOUNT,
	//持仓回报
	FEEDBACK_POSITION,
	//成交回报
	FEEDBACK_TRADE,
	//CTP连接成功
	GATEWAY_CTP_CONNECTED,
	//CTP准备完成
	GATEWAY_CTP_READY,
	
}
