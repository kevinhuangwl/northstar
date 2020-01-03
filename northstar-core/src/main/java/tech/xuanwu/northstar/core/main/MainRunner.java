package tech.xuanwu.northstar.core.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.gateway.GatewayApi;

@Slf4j
@Component
public class MainRunner implements CommandLineRunner{

	@Autowired
	GatewayApi ctpGatewayApi;
	
	@Override
	public void run(String... args) throws Exception {
		
		for(;;) {
			if(!ctpGatewayApi.isConnected()) {
				ctpGatewayApi.connect();
			}else {
				log.info("CTP接口已连通");
				break;
			}
			
			Thread.sleep(3000);
		}
	}

}
