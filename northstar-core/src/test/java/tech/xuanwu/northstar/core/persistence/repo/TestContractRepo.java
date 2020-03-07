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
	
	
	@Test
	public void test() throws Exception {
		ContractInfo info1 = new ContractInfo();
		info1.setSymbol("rb2005");
		info1.setLastTradeDateOrContractMonth("20200101");
		
		ContractInfo info2 = new ContractInfo();
		info2.setSymbol("rb2006");
		info2.setLastTradeDateOrContractMonth("20900101");
		
		ContractInfo info3 = new ContractInfo();
		info3.setSymbol("rb2007");
		info3.setLastTradeDateOrContractMonth("20900101");
		info3.setSubscribed(true);
		
		contractRepo.upsert(info1);
		contractRepo.upsert(info2);
		contractRepo.upsert(info3);
		
		assertThat(contractRepo.getAllAvailableContracts().size()).isEqualTo(2);
		assertThat(contractRepo.getAllSubscribedContracts().size()).isEqualTo(1);
	}
}
