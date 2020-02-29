package tech.xuanwu.northstar.domain;

import java.util.List;

import tech.xuanwu.northstar.entity.AccountInfo;
import xyz.redtorch.pb.CoreField.ContractField;

/**
 * 账户接口
 * @author kevinhuangwl
 *
 */
public interface IAccount extends TradableAccount {
	
	String getName();
	
	/**
	 * 获取账户信息
	 * @return
	 */
	AccountInfo getAccountInfo();
	
	/**
	 * 更新账户
	 * @param account
	 */
	void updateAccount(AccountInfo account);
	
	/**
	 * 获取注册策略列表
	 * @return
	 */
	List<IStrategy> getStrategyList();
	
	/**
	 * 注册策略
	 * @param strategyName
	 */
	void regStrategy(String strategyName);
	
	/**
	 * 注销策略
	 * @param strategyName
	 */
	void unregStrategy(String strategyName);
	
	/**
	 * 一键全平
	 */
	void sellOutAllPosition();
	
	/**
	 * 连线网关
	 */
	void connectGateway();
	
	/**
	 * 断开网关
	 */
	void disconnectGateway();
	
	/**
	 * 订阅合约
	 * @param contract
	 * @return
	 */
	boolean subscribe(ContractField contract);
	
}
