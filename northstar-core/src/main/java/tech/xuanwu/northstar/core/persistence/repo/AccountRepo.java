package tech.xuanwu.northstar.core.persistence.repo;

import tech.xuanwu.northstar.entity.AccountInfo;

public interface AccountRepo {

	boolean upsert(AccountInfo account) throws Exception;
	
	AccountInfo getLatestAccountInfoByName(String accountName) throws Exception;
}
