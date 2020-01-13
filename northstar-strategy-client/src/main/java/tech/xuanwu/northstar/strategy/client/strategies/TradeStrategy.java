package tech.xuanwu.northstar.strategy.client.strategies;

/**
 * 交易策略接口
 * @author kevinhuangwl
 *
 */
public interface TradeStrategy {

	/**
	 * 策略初始化
	 */
	void init();
	
	/**
	 * 启用策略
	 * @return
	 */
	boolean resume();
	
	/**
	 * 停用策略
	 * @return
	 */
	boolean suspend();
}
