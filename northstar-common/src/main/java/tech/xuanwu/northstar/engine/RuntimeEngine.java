package tech.xuanwu.northstar.engine;

import java.util.EventObject;
import java.util.List;

import tech.xuanwu.northstar.constant.RuntimeEvent;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.entity.AccountInfo;
import tech.xuanwu.northstar.exception.NoSuchAccountException;
import tech.xuanwu.northstar.exception.NoSuchEventHandlerException;

/**
 * 运行时引擎
 * 负责提供领域对象的运行时环境
 * @author kevinhuangwl
 *
 */
public interface RuntimeEngine {
	
	void regAccount(IAccount account);
	
	void unregAccount(String accountGatewayId);
	
	IAccount getAccount(String accountGatewayId) throws NoSuchAccountException;
	
	List<AccountInfo> getAccountInfoList();
	
	boolean addEventHandler(RuntimeEvent event, Listener listener);
	
	void emitEvent(RuntimeEvent event, EventObject e) throws NoSuchEventHandlerException;
	
	interface Listener{
		
		void onEvent(EventObject e) throws Exception;
	}
	
}
