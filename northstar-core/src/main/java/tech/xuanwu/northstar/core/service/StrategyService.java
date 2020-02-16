package tech.xuanwu.northstar.core.service;

import java.util.List;

import tech.xuanwu.northstar.dto.StrategyInfo;

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
	List<StrategyInfo> getStrategyInfoList();
}
