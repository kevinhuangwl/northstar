package tech.xuanwu.northstar.core.engine.rt.event;

import java.util.EventObject;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.EventEnum;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.dto.StrategyInfo;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.exception.NoSuchAccountException;

@Slf4j
@Component
public class StrategyRegEventHandler implements RuntimeEngine.Listener, InitializingBean{
	
	@Autowired
	private RuntimeEngine rtEngine;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		rtEngine.addEventHandler(EventEnum.REGISTER_STRATEGY.toString(), this);
	}

	@Override
	public void onEvent(EventObject e) {
		try {
			StrategyInfo s = (StrategyInfo) e.getSource();
			IAccount account = rtEngine.getAccount(s.getAccountName());
	    	account.regStrategy(s.getStrategyName());	
		}catch(Exception ex) {
			log.error("", ex);
		}
			
	}

}
