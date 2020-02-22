package tech.xuanwu.northstar.core.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.entity.AccountInfo;
import tech.xuanwu.northstar.exception.NoSuchAccountException;

public abstract class AbstractTraderRuntimeEngine implements RuntimeEngine{
	
	protected ConcurrentHashMap<String, IAccount> accountMap = new ConcurrentHashMap<>();

	@Override
	public void regAccount(IAccount account) {
		accountMap.put(account.getName(), account);
	}
	
	@Override
	public void unregAccount(String accountName) {
		accountMap.remove(accountName);
	}
	
	@Override
	public IAccount getAccount(String accountName) throws NoSuchAccountException {
		IAccount account = accountMap.get(accountName);
		if(account == null) {
			throw new NoSuchAccountException(accountName);
		}
		return account;
	}

	@Override
	public List<AccountInfo> getAccountInfoList() {
		List<AccountInfo> resultList = new ArrayList<AccountInfo>();
		for(Entry<String, IAccount> e : accountMap.entrySet()) {
			IAccount account = e.getValue();
			resultList.add(account.getAccountInfo());
		}
		return resultList;
	}
	
}
