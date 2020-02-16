package tech.xuanwu.northstar.engine;

import java.util.EventObject;
import java.util.List;

import tech.xuanwu.northstar.domain.IAccount;
import xyz.redtorch.pb.CoreField.AccountField;

/**
 * 运行时引擎
 * 负责提供领域对象的运行时环境
 * @author kevinhuangwl
 *
 */
public interface RuntimeEngine {
	
	void regAccount(IAccount account);
	
	void unregAccount(String accountName);
	
	IAccount getAccount(String accountName) throws IllegalArgumentException;
	
	List<AccountField> getAccountInfoList();
	
	boolean addEventHandler(String event, Listener listener);
	
	void emitEvent(String event, EventObject e) throws IllegalStateException;
	
	interface Listener{
		
		void onEvent(EventObject e);
	}
	
}
