package tech.xuanwu.northstar.engine;

import java.util.EventObject;
import java.util.List;

import tech.xuanwu.northstar.constant.RuntimeEvent;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.exception.NoSuchAccountException;
import tech.xuanwu.northstar.exception.NoSuchEventHandlerException;

/**
 * 运行时引擎
 * 负责管理账户、策略等交易领域模型对象
 * @author kevinhuangwl
 *
 */
public interface RuntimeEngine {
	
	void regAccount(IAccount account);
	
	void unregAccount(String accountName);
	
	IAccount getAccount(String accountName) throws NoSuchAccountException;
	
	List<String> getAccountNameList();
	
	boolean addEventHandler(RuntimeEvent event, Listener listener);
	
	void emitEvent(RuntimeEvent event, EventObject e) throws NoSuchEventHandlerException;
	
	interface Listener{
		
		void onEvent(EventObject e) throws Exception;
	}
	
}
