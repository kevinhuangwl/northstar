package tech.xuanwu.northstar.core.config.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.service.AccountService;

@Slf4j
@Component
public class AccountStarter implements CommandLineRunner{
	
	@Autowired
	RuntimeEngine rtEngine;
	
	@Autowired
	AccountService accountService;

	@Override
	public void run(String... args) throws Exception {
		log.info("启动账户连接，自动连接所有账户");
		for(IAccount ac : rtEngine.getAccountList()) {
			accountService.connect(ac.getAccountId());
		}
	}

}
