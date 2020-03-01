package tech.xuanwu.northstar.core.engine.rt.event;

import java.util.EventObject;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.RuntimeEvent;
import tech.xuanwu.northstar.core.util.FutureDictionary;
import tech.xuanwu.northstar.engine.RuntimeEngine;

@Slf4j
@Component
public class CtpConnectedEventHandler implements RuntimeEngine.Listener, InitializingBean{

	@Autowired
	private RuntimeEngine rtEngine;
	
	@Autowired
	private FutureDictionary fDict;

	@Override
	public void afterPropertiesSet() throws Exception {
		rtEngine.addEventHandler(RuntimeEvent.GATEWAY_CTP_CONNECTED, this);
	}
	

	@Override
	public void onEvent(EventObject e) throws Exception {
		fDict.clear();
		log.info("重置合约字典");
	}

}
