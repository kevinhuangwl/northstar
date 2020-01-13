package tech.xuanwu.northstar.strategy.client.service;

import java.util.List;

import tech.xuanwu.northstar.common.ResultBean;
import tech.xuanwu.northstar.strategy.client.strategies.TradeStrategy;

/**
 * 策略服务接口
 * @author kevinhuangwl
 *
 */
public interface StrategyService {

	void addStrategy(TradeStrategy s);

	ResultBean<List<TradeStrategy>> getStrategyList();
	
	ResultBean<Boolean> resumeStrategy(String name);
	
	ResultBean<Boolean> suspendStrategy(String name);
	
	ResultBean<Boolean> suspendAllStrategies();
}
