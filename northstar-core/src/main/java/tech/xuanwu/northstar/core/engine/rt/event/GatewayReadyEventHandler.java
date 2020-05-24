package tech.xuanwu.northstar.core.engine.rt.event;

import java.util.EventObject;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.RuntimeEvent;
import tech.xuanwu.northstar.core.persistence.repo.ContractRepo;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.IndexEngine;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.entity.ContractInfo;
import tech.xuanwu.northstar.gateway.GatewayApi;
import xyz.redtorch.pb.CoreField.ContractField;
import xyz.redtorch.pb.CoreField.GatewayField;

@Slf4j
@Component
public class GatewayReadyEventHandler implements RuntimeEngine.Listener, InitializingBean{

	@Autowired
	private RuntimeEngine rtEngine;
	
	@Autowired
	private IndexEngine idxEngine;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		rtEngine.addEventHandler(RuntimeEvent.GATEWAY_READY, this);
	}

	@Override
	public void onEvent(EventObject e) throws Exception {
		String gatewayEncodedBase64Str = (String) e.getSource();
		GatewayField gateway = GatewayField.parseFrom(Base64.decodeBase64(gatewayEncodedBase64Str));
		log.info("网关[{}]，连接成功", gateway.getName());
		IAccount account = rtEngine.getAccount(gateway.getName());
		account.onConnected();
		
		//自动续订指数合约
		idxEngine.onGatewayReady(gateway.getGatewayId());
	}

}
