package tech.xuanwu.northstar.core.engine.rt.event;

import java.util.EventObject;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.CommonConstant;
import tech.xuanwu.northstar.constant.RuntimeEvent;
import tech.xuanwu.northstar.core.persistence.repo.ContractRepo;
import tech.xuanwu.northstar.core.persistence.repo.GatewayRepo;
import tech.xuanwu.northstar.engine.IndexEngine;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.entity.ContractInfo;
import tech.xuanwu.northstar.entity.GatewayInfo;
import tech.xuanwu.northstar.gateway.GatewayApi;
import xyz.redtorch.pb.CoreEnum.ConnectStatusEnum;
import xyz.redtorch.pb.CoreField.ContractField;

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
		//留一下回调备用
	}

}
