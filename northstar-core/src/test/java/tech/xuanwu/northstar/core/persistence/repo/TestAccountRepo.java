package tech.xuanwu.northstar.core.persistence.repo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import tech.xuanwu.northstar.core.config.MongoDBConfig;
import tech.xuanwu.northstar.entity.AccountInfo;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes= {AccountRepoImpl.class, MongoDBConfig.class})
@SpringBootTest
public class TestAccountRepo {

	@Autowired
	AccountRepo accountRepo;
	
	String accountName = "test_account";
	
	
	@Test
	public void test() throws Exception {
		
		AccountInfo a1 = new AccountInfo();
		a1.setName(accountName);
		a1.setGatewayId(accountName);
		a1.setTradingDay("20200131");
		accountRepo.upsertByDate(a1);
		
		a1.setBalance(100);
		accountRepo.upsertByDate(a1);
		
		AccountInfo info1 = accountRepo.getLatestAccountInfoByGatewayId(accountName);
		assertThat(info1).isEqualTo(a1);
	} 
	
}
