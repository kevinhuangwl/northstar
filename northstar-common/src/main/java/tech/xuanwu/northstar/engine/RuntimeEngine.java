package tech.xuanwu.northstar.engine;

import java.util.EventObject;

import tech.xuanwu.northstar.domain.IAccount;

/**
 * 运行时引擎
 * 负责提供领域对象的运行时环境
 * @author kevinhuangwl
 *
 */
public interface RuntimeEngine {
	
	void regAccount(IAccount account);
	
	void unregAccount(String accountName);
	
	IAccount getAccount(String accountName);
	
	
	boolean addEventHandler(String event, Listener listener);
	
	void emitEvent(String event, EventObject e) throws IllegalStateException;
	
	interface Listener{
		
		void onEvent(EventObject e);
	}
	
}
