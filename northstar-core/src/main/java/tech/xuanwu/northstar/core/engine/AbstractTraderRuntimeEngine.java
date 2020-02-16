package tech.xuanwu.northstar.core.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.RuntimeEngine;
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
	public IAccount getAccount(String accountName) {
		IAccount account = accountMap.get(accountName);
		if(account == null) {
			throw new IllegalArgumentException("没有找到账户名为【" + accountName + "】的账户");
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
