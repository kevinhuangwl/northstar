package tech.xuanwu.northstar.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.core.persistence.repo.ContractRepo;
import tech.xuanwu.northstar.core.service.AccountService;
import tech.xuanwu.northstar.core.service.MarketDataService;
import tech.xuanwu.northstar.core.util.FutureDictionary;
import tech.xuanwu.northstar.entity.ContractInfo;
import xyz.redtorch.pb.CoreField.ContractField;

@Slf4j
@Component
public class AutoStartUpRunner implements CommandLineRunner{
	
	@Autowired
	private AccountService accService;
	
	@Autowired
	private MarketDataService mdService;
	
	@Autowired
	private ContractRepo contractRepo;
	
	@Autowired
	private FutureDictionary fDict;

	@Override
	public void run(String... args) throws Exception {
		
		//自动连接网关
		accService.connectGateway();
		
		//等待网关连接完成
		Thread.sleep(5000);
		
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
		
	}

}
