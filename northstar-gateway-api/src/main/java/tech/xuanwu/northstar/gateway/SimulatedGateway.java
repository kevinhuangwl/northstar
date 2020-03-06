package tech.xuanwu.northstar.gateway;

import tech.xuanwu.northstar.entity.AccountInfo;

public interface SimulatedGateway {

	/**
	 * 初始化账户
	 * @param account
	 */
	void initGatewayAccount(AccountInfo account);
	
	/**
	 * 进行日结算
	 */
	void proceedDailySettlement();
}
