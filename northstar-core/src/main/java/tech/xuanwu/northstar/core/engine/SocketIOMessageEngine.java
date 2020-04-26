package tech.xuanwu.northstar.core.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.Message;
import tech.xuanwu.northstar.engine.MessageEngine;
import tech.xuanwu.northstar.entity.StrategyInfo;
import tech.xuanwu.northstar.service.AccountService;
import tech.xuanwu.northstar.service.MarketDataService;
import xyz.redtorch.pb.CoreField.TickField;

@Slf4j
@Component
public class SocketIOMessageEngine implements MessageEngine{
	
	/**************************************************/
	/*					消息发送端						  */
	/**************************************************/
	@Autowired
	SocketIOServer server;
	
	@Override
	public void emitTick(TickField tick) {
		//以合约名称作为广播的房间号
		String unifiedSymbol = tick.getUnifiedSymbol();
		String symbol = unifiedSymbol.split("@")[0];
		
		server.getRoomOperations(symbol).sendEvent(Message.MARKET_DATA, tick.toByteArray());
	}
	
	
	
	
	
	
	/**************************************************/
	/*					消息接收端						  */
	/**************************************************/
	
	@Autowired
	MarketDataService mdService;
	
	@Autowired
	AccountService accountService;
	
	@OnConnect  
    private void onConnect(final SocketIOClient client) {
    	log.info("【策略连接】-[{}],已连接", client.getSessionId());
    }  
  
    @OnDisconnect  
    private void onDisconnect(final SocketIOClient client) {
    	log.info("【策略断开】-[{}],断开连接", client.getSessionId());
    }
    
    @OnEvent(Message.REG_STRATEGY)
    private void onRegisterStrategy(final SocketIOClient client, StrategyInfo s) throws Exception {
    	
    	log.info("【策略注册】-[{}],策略：{}，绑定账户：{}，订阅合约：{}", 
    			client.getSessionId(), 
    			s.getStrategyName(), 
    			s.getAccountName(), 
    			JSON.toJSONString(s.getSubscribeContracts()));
    	
    	String[] symbolList = s.getSubscribeContracts();
    	
    	//订阅合约
    	for(String symbol : symbolList) {
    		client.joinRoom(symbol);
    		mdService.subscribeContract(s.getGatewayId(), symbol);
    	}
    }
}
