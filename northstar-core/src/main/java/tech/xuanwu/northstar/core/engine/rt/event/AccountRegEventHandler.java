package tech.xuanwu.northstar.core.engine.rt.event;

import java.util.EventObject;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.EventType;
import tech.xuanwu.northstar.engine.RuntimeEngine;

@Slf4j
@Component
public class AccountRegEventHandler implements RuntimeEngine.Listener, InitializingBean{
	
	@Autowired
	private RuntimeEngine rtEngine;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		rtEngine.addEventHandler(EventType.ACCOUNT_REGISTER.toString(), this);
	}

	@Override
	public void onEvent(EventObject e) {
		// TODO Auto-generated method stub
		
	}

}
