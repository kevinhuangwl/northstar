package tech.xuanwu.northstar.domain;

import java.util.List;

import xyz.redtorch.pb.CoreField.AccountField;

/**
 * 账户接口
 * @author kevinhuangwl
 *
 */
public interface IAccount extends IAccountLike{
	
	AccountField getAccount();
	
	void updateAccount(AccountField account);
	
	List<IStrategy> getStrategyList();
	
	void regStrategy(String strategyName);
	
	void unregStrategy(String strategyName);
	
	void sellOutAllPosition();
}
