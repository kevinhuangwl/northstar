package tech.xuanwu.northstar.service;

import java.util.List;

import tech.xuanwu.northstar.dto.StrategyInfo;

/**
 * 策略服务，用于提供控制策略的操作
 * @author kevinhuangwl
 *
 */
public interface StrategyService {

	/**
	 * 注册策略
	 * @param s
	 * @return
	 */
	boolean registerStrategy(StrategyInfo s);
	
	/**
	 * 启用策略
	 * @param strategyName
	 * @return
	 */
	boolean enableStrategy(String strategyName);
	
	/**
	 * 停用策略
	 * @param strategyName
	 * @return
	 */
	boolean disableStrategy(String strategyName);
	
	/**
	 * 策略全停
	 * @return
	 */
	boolean disableAllStrategies();
	
	/**
	 * 获取策略信息
	 * @return
	 */
	List<StrategyInfo> getStrategyInfos();
}
