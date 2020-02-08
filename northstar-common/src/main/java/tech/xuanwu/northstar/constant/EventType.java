package tech.xuanwu.northstar.constant;

/**
 * 运行时引擎事件枚举
 * @author kevinhuangwl
 *
 */
public enum EventType {
	
	//更新TICK数据
	TICK_UPDATE,
	//注册账户
	REGISTER_ACCOUNT,
	//注册策略
	REGISTER_STRATEGY,
	//注册合约
	REGISTER_CONTRACT,
	//下单
	SUBMIT_ORDER,
	//撤单
	WITHDRAW_ORDER,
	
	
	GATEWAY_CONNECTED,
	
	
}
