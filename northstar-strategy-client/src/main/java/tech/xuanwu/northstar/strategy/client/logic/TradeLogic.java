package tech.xuanwu.northstar.strategy.client.logic;

/**
 * 交易逻辑接口
 * @author kevinhuangwl
 *
 */
public interface TradeLogic {

	Signal getCurrentSignal();
	
	/**
	 * 交易信号
	 * @author kevinhuangwl
	 *
	 */
	enum Signal{
		/**
		 * 无信号
		 */
		NONE,
		
		/**
		 * 多头信号
		 */
		LONG,
		
		/**
		 * 空头信号
		 */
		SHORT;
	}
}
