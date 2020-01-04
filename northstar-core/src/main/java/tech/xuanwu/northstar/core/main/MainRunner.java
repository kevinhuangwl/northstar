package tech.xuanwu.northstar.core.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.SocketIOServer;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.gateway.GatewayApi;

@Slf4j
@Component
public class MainRunner implements CommandLineRunner{

	@Autowired
	GatewayApi ctpGatewayApi;
	
	@Autowired
	SocketIOServer socketioServer; 
	
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
		
		socketioServer.start();
	}

}
