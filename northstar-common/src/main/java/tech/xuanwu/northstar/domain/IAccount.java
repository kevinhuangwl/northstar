package tech.xuanwu.northstar.domain;

import java.util.List;

import xyz.redtorch.pb.CoreField.AccountField;

/**
 * 账户接口
 * @author kevinhuangwl
 *
 */
public interface IAccount extends Tradable {
	
	AccountField getAccountInfo();
	
	void updateAccount(AccountField account);
	
	List<IStrategy> getStrategyList();
	
	void regStrategy(String strategyName);
	
	void unregStrategy(String strategyName);
	
	void sellOutAllPosition();
}
