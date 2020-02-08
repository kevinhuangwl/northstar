package tech.xuanwu.northstar.core.engine.rt.event;

import java.util.EventObject;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.EventType;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.dto.StrategyInfo;
import tech.xuanwu.northstar.engine.RuntimeEngine;

@Slf4j
@Component
public class StrategyRegEventHandler implements RuntimeEngine.Listener, InitializingBean{
	
	@Autowired
	private RuntimeEngine rtEngine;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		rtEngine.addEventHandler(EventType.REGISTER_STRATEGY.toString(), this);
	}

	@Override
	public void onEvent(EventObject e) {
		try {
			StrategyInfo s = (StrategyInfo) e.getSource();
			IAccount account = rtEngine.getAccount(s.getAccountName());
	    	if(account==null) {
	    		log.warn("没有名称为{}的注册账户", s.getAccountName());
	    		return;
	    	}
	    	account.regStrategy(s.getStrategyName());	
		}catch(ClassCastException ex) {
			log.error("", ex);
		}
			
	}

}
