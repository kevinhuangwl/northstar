package tech.xuanwu.northstar.core.service;

import java.util.List;

import tech.xuanwu.northstar.entity.AccountInfo;

/**
 * 模拟账户管理接口
 * @author kevinhuangwl
 *
 */
public interface SimulateAccountManagementService {

	/**
	 * 创建账户
	 * @param accountName
	 * @param holderName
	 * @param gatewayId
	 * @param initialBalance
	 * @param commissionRate
	 * @param marginRate
	 * @return
	 */
	boolean createAccount(String accountName, String holderName, String gatewayId, double initialBalance, double commissionRate, double marginRate);
	
	/**
	 * 设置手续费率
	 * @param rate
	 */
	void setCommissionRate(String accountName, double rate) throws Exception;
	
	/**
	 * 设置保证金率
	 * @param rate
	 */
	void setMarginRate(String accountName, double rate) throws Exception;
	
	/**
	 * 移除账户
	 * @param accountName
	 * @return
	 */
	boolean dropAccount(String accountName) throws Exception;
	
	/**
	 * 入金
	 * @param money	
	 * @return	返回入金后的账户余额
	 */
	double depositMoney(String accountName, double money) throws Exception;
	
	/**
	 * 出金
	 * @param money
	 * @return	返回出金后的账户余额
	 */
	double withdrawMoney(String accountName, double money) throws Exception;
	
	/**
	 * 获取所有模拟账户
	 * @return
	 */
	List<AccountInfo> getAllSimulateAccounts();
}
