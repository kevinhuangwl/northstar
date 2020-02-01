package tech.xuanwu.northstar.core.dao;

import java.time.LocalDate;

import xyz.redtorch.pb.CoreField.AccountField;

public interface AccountDao {

	/**
	 * 添加账户记录
	 * @param account
	 * @return
	 */
	boolean insert(AccountField account);
	
	/**
	 * 根据账户ID获取最近一条账户记录
	 * @param accountId
	 * @return
	 */
	AccountField getLatestRecord(String accountId);
	
	/**
	 * 根据账户ID获取一定时期内的账户记录
	 * @param accountId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	AccountField[] getRecordsByPeriod(String accountId, LocalDate startDate, LocalDate endDate);
}
