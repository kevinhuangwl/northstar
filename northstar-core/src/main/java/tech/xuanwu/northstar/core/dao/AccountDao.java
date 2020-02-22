package tech.xuanwu.northstar.core.dao;

import java.time.LocalDate;

import tech.xuanwu.northstar.entity.AccountInfo;

public interface AccountDao {

	/**
	 * 添加账户记录
	 * @param account
	 * @return
	 */
	boolean insert(AccountInfo account);
	
	/**
	 * 根据账户ID获取最近一条账户记录
	 * @param accountId
	 * @return
	 */
	AccountInfo getLatestRecord(String accountId);
	
	/**
	 * 根据账户ID获取一定时期内的账户记录
	 * @param accountId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	AccountInfo[] getRecordsByPeriod(String accountId, LocalDate startDate, LocalDate endDate);
}
