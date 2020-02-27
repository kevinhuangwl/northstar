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
	
	AccountInfo getAccountInfo();
	
	void updateAccount(AccountInfo account);
	
	List<IStrategy> getStrategyList();
	
	void regStrategy(String strategyName);
	
	void unregStrategy(String strategyName);
	
	void sellOutAllPosition();
	
	void connectGateway();
	
	void disconnectGateway();
	
	boolean subscribe(ContractField contract);
}
