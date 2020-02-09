package tech.xuanwu.northstar.core.engine;

import java.util.EventObject;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.RuntimeEngine;

@Slf4j
@Component
public class NorthstarRuntimeEngine extends AbstractTraderRuntimeEngine implements RuntimeEngine{
	
	
	private ConcurrentHashMap<String, ConcurrentLinkedQueue<Listener>> handlerMap = new ConcurrentHashMap<>();
	
	@Override
	public boolean addEventHandler(String event, Listener listener) {
		if(!handlerMap.containsKey(event)) {
			handlerMap.put(event, new ConcurrentLinkedQueue<RuntimeEngine.Listener>());
		}
		
		log.info("增加一个【{}】事件的处理函数:{}", event, listener.getClass().getSimpleName());
		return handlerMap.get(event).add(listener);
	}


	@Override
	public void emitEvent(String event, EventObject e) throws IllegalStateException {
		if(!handlerMap.containsKey(event)) {
			throw new IllegalStateException("没有事件【" + event + "】相应的处理函数");
		}
		
		ConcurrentLinkedQueue<Listener> q = handlerMap.get(event);
		Iterator<Listener> it = q.iterator();
		while(it.hasNext()) {
			it.next().onEvent(e);
		}
	}


}
