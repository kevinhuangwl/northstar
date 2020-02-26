package tech.xuanwu.northstar.core.engine.rt.event;

import java.util.EventObject;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.RuntimeEvent;
import tech.xuanwu.northstar.core.persistence.repo.ContractRepo;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import xyz.redtorch.pb.CoreField.ContractField;

@Slf4j
@Component
public class ContractRegEventHandler implements RuntimeEngine.Listener, InitializingBean{

	@Autowired
	private RuntimeEngine rtEngine;
	
	@Autowired
	private ContractRepo contractRepo;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		rtEngine.addEventHandler(RuntimeEvent.REGISTER_CONTRACT, this);
	}

	@Override
	public void onEvent(EventObject e) {
		try {			
			ContractField c = (ContractField) e.getSource();
			contractRepo.upsert(c);
		}catch(Exception ex) {
			log.error("", ex);
		}
	}
}
