package tech.xuanwu.northstar.core.engine.rt.event;

import java.util.EventObject;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.RuntimeEvent;
import tech.xuanwu.northstar.core.persistence.repo.ContractRepo;
import tech.xuanwu.northstar.core.service.MarketDataService;
import tech.xuanwu.northstar.core.util.FutureDictionary;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.entity.ContractInfo;
import xyz.redtorch.pb.CoreField.ContractField;

@Slf4j
@Component
public class CtpReadyEventHandler implements RuntimeEngine.Listener, InitializingBean{

	@Autowired
	private RuntimeEngine rtEngine;
	
	@Autowired
	private MarketDataService mdService;
	
	@Autowired
	private ContractRepo contractRepo;
	
	@Autowired
	private FutureDictionary fDict;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		rtEngine.addEventHandler(RuntimeEvent.GATEWAY_CTP_READY, this);
		
	}

	@Override
	public void onEvent(EventObject e) throws Exception {
		log.info("合约字典的合约总数：{}", fDict.size());
		log.info("=====开始自动续订合约=====");
		//自动续订阅合约
		for(ContractInfo c : mdService.getAllSubscribedContracts()) {
			ContractField contract = fDict.getContractByName(c.getSymbol());
			if(contract != null) {
				mdService.subscribeContract(c.getGatewayId(), c.getSymbol());
				log.info("订阅网关【{}】的合约【{}】", c.getGatewayId(), c.getSymbol());
			}else {
				log.warn("合约【{}】已过期", c.getSymbol());
				contractRepo.delete(c);				
			}
		}		
		log.info("=====自动续订合约完成=====");
	}

}
