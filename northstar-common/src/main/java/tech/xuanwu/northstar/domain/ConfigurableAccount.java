package tech.xuanwu.northstar.domain;

public interface ConfigurableAccount {

	/**
	 * 入金
	 * @param money	
	 * @return	返回入金后的账户余额
	 */
	double depositMoney(double money) throws Exception;
	
	/**
	 * 出金
	 * @param money
	 * @return	返回出金后的账户余额
	 */
	double withdrawMoney(double money) throws Exception;
	
}
