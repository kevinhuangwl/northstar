package tech.xuanwu.northstar.core.engine;

import java.util.concurrent.ConcurrentHashMap;

import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.RuntimeEngine;

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
	public IAccount getAccount(String accountName) {
		return accountMap.get(accountName);
	}
}
