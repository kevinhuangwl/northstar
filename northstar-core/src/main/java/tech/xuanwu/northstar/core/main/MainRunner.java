package tech.xuanwu.northstar.core.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.SocketIOServer;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.gateway.GatewayApi;

/**
 * 业务启动入口
 * @author kevinhuangwl
 *
 */
@Slf4j
@Component
public class MainRunner implements CommandLineRunner{

	@Autowired
	GatewayApi ctpGatewayApi;
	
	@Autowired
	SocketIOServer socketioServer; 
	
	@Override
	public void run(String... args) throws Exception {
		
		//自动连接交易账户
		for(;;) {
			if(!ctpGatewayApi.isConnected()) {
				ctpGatewayApi.connect();
			}else {
				log.info("CTP接口已连通");
				break;
			}
			
			Thread.sleep(3000);
		}
		
//		ContractField.Builder cb = ContractField.newBuilder();
//		cb.setSymbol("rb2005");
//		ctpGatewayApi.subscribe(cb.build());
		
		//启动socket服务
		socketioServer.start();
	}

}
