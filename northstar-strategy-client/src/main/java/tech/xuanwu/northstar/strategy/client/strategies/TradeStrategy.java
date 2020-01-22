package tech.xuanwu.northstar.strategy.client.strategies;

/**
 * 交易策略接口
 * @author kevinhuangwl
 *
 */
public interface TradeStrategy {

	
	/**
	 * 启用策略
	 * @return
	 */
	void resume();
	
	/**
	 * 停用策略
	 * @return
	 */
	void suspend();
	
	
	/**
	 * 是否运行
	 * @return
	 */
	boolean isRunning();
}
