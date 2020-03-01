package tech.xuanwu.northstar.core.persistence.repo;

import tech.xuanwu.northstar.entity.AccountInfo;

public interface AccountRepo {

	/**
	 * 依据交易日保存更新合约信息
	 * @param account
	 * @param tradingDay
	 * @return
	 * @throws Exception
	 */
	boolean upsertByDay(AccountInfo account, String tradingDay) throws Exception;
	
	/**
	 * 依据账户名称获取最近交易日账户信息
	 * @param accountName
	 * @return
	 * @throws Exception
	 */
	AccountInfo getLatestAccountInfoByName(String accountName) throws Exception;
}
