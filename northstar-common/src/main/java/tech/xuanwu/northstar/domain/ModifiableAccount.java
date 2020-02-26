package tech.xuanwu.northstar.domain;

public interface ModifiableAccount {

	/**
	 * 设置手续费率
	 * @param rate
	 */
	void setCommissionRate(double rate) throws Exception;
	
	/**
	 * 设置保证金率
	 * @param rate
	 */
	void setMarginRate(double rate) throws Exception;
	
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
