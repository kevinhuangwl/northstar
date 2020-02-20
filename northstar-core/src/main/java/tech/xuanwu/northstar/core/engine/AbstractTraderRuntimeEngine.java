package tech.xuanwu.northstar.core.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.exception.NoSuchAccountException;
import xyz.redtorch.pb.CoreField.AccountField;

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
	public List<AccountField> getAccountInfoList() {
		List<AccountField> resultList = new ArrayList<AccountField>();
		for(Entry<String, IAccount> e : accountMap.entrySet()) {
			IAccount account = e.getValue();
			resultList.add(account.getAccountInfo());
		}
		return resultList;
	}
	
}
