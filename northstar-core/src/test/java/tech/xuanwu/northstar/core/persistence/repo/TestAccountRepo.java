package tech.xuanwu.northstar.core.persistence.repo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import tech.xuanwu.northstar.core.config.MongoDBConfig;
import tech.xuanwu.northstar.core.config.props.MongoDBSettings;
import tech.xuanwu.northstar.entity.AccountInfo;
import xyz.redtorch.common.mongo.MongoDBClient;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes= {AccountRepoImpl.class, MongoDBConfig.class, MongoDBSettings.class})
@SpringBootTest
public class TestAccountRepo {

	@Autowired
	AccountRepo accountRepo;
	
	MongoDBClient mgClient;
	
	String accountName = "test_account";
	
	
	@Test
	public void test() throws Exception {
		
		AccountInfo a1 = new AccountInfo();
		a1.setName(accountName);
		a1.setTradingDay("20200131");
		AccountInfo a2 = new AccountInfo();
		a2.setName(accountName);
		a2.setTradingDay("20200202");
		accountRepo.upsertByDay(a1, "20200202");
		
		AccountInfo info1 = accountRepo.getLatestAccountInfoByName(accountName);
		assertThat(info1).isEqualTo(a2);
	} 
	
}
