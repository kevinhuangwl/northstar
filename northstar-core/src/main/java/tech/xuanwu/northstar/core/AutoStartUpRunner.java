package tech.xuanwu.northstar.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import tech.xuanwu.northstar.service.AccountService;

@Component
public class AutoStartUpRunner implements CommandLineRunner{
	
	@Autowired
	private AccountService accService;
	
	

	@Override
	public void run(String... args) throws Exception {
		
		//自动连接网关
		accService.connectGateway();
	}

}
