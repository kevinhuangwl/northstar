package tech.xuanwu.northstar.core.persistence.repo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import tech.xuanwu.northstar.core.config.MongoDBConfig;
import tech.xuanwu.northstar.entity.ContractInfo;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes= {ContractRepoImpl.class, MongoDBConfig.class})
@SpringBootTest
public class TestContractRepo {

	@Autowired
	private ContractRepo contractRepo;
	
	String gatewayId = "GW";
	
	
	@Test
	public void test() throws Exception {
		ContractInfo info1 = new ContractInfo();
		info1.setContractId("rb2005");
		info1.setSymbol("rb2005");
		info1.setLastTradeDateOrContractMonth("20200101");
		info1.setGatewayId(gatewayId);
		
		ContractInfo info2 = new ContractInfo();
		info2.setContractId("rb2006");
		info2.setSymbol("rb2006");
		info2.setLastTradeDateOrContractMonth("20900101");
		info2.setGatewayId(gatewayId);
		
		ContractInfo info3 = new ContractInfo();
		info3.setContractId("rb2007");
		info3.setSymbol("rb2007");
		info3.setLastTradeDateOrContractMonth("20900101");
		info3.setSubscribed(true);
		info3.setGatewayId(gatewayId);
		
		contractRepo.insertIfAbsent(info1);
		contractRepo.insertIfAbsent(info2);
		contractRepo.insertIfAbsent(info3);
		
		assertThat(contractRepo.getAllAvailableContracts(gatewayId).size()).isEqualTo(2);
		assertThat(contractRepo.getAllSubscribedContracts(gatewayId).size()).isEqualTo(1);
		
		info3.setSubscribed(false);
		assertThat(contractRepo.insertIfAbsent(info3)).isFalse();
		assertThat(contractRepo.getAllSubscribedContracts(gatewayId).size()).isEqualTo(1);
		
		
	}
}
