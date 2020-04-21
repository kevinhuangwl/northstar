package tech.xuanwu.northstar.core.engine;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.RuntimeEvent;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.exception.NoSuchAccountException;
import tech.xuanwu.northstar.exception.NoSuchEventHandlerException;

@Slf4j
@Component
public class NorthstarRuntimeEngine implements RuntimeEngine{
	
	
	ConcurrentHashMap<RuntimeEvent, ConcurrentLinkedQueue<Listener>> handlerMap = new ConcurrentHashMap<>();
	
	ConcurrentHashMap<String, IAccount> accountMap = new ConcurrentHashMap<>();

	@Override
	public void regAccount(IAccount account) {
		accountMap.put(account.getAccountId(), account);
	}
	
	@Override
	public IAccount getAccount(String accountId) throws NoSuchAccountException {
		IAccount account = accountMap.get(accountId);
		if(account == null) {
			throw new NoSuchAccountException(accountId);
		}
		return account;
	}

	@Override
	public boolean addEventHandler(RuntimeEvent event, Listener listener) {
		if(!handlerMap.containsKey(event)) {
			handlerMap.put(event, new ConcurrentLinkedQueue<RuntimeEngine.Listener>());
		}
		
		log.info("增加一个【{}】事件的处理函数:{}", event, listener.getClass().getSimpleName());
		return handlerMap.get(event).add(listener);
	}


	@Override
	public void emitEvent(RuntimeEvent event, EventObject e) throws NoSuchEventHandlerException {
		if(handlerMap.get(event)==null) {
			throw new NoSuchEventHandlerException(event);
		}
		
		ConcurrentLinkedQueue<Listener> q = handlerMap.get(event);
		Iterator<Listener> it = q.iterator();
		while(it.hasNext()) {
			try {
				it.next().onEvent(e);
			} catch (Exception ex) {
				log.error("", ex);
			}
		}
	}

	@Override
	public List<String> getAccountIdList() {
		List<String> resultList = new ArrayList<String>(accountMap.size());
		resultList.addAll(accountMap.keySet());
		return resultList;
	}


}
