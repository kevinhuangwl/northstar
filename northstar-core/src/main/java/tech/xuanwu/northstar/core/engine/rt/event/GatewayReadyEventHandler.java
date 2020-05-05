package tech.xuanwu.northstar.core.engine.rt.event;

import java.util.EventObject;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.RuntimeEvent;
import tech.xuanwu.northstar.engine.RuntimeEngine;

@Slf4j
@Component
public class GatewayReadyEventHandler implements RuntimeEngine.Listener, InitializingBean{

	@Autowired
	private RuntimeEngine rtEngine;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		rtEngine.addEventHandler(RuntimeEvent.GATEWAY_READY, this);
	}

	@Override
	public void onEvent(EventObject e) throws Exception {
		String gatewayName = (String) e.getSource();
		log.info("网关[{}]，连接成功", gatewayName);
		rtEngine.getAccount(gatewayName).onConnected();
	}

}
