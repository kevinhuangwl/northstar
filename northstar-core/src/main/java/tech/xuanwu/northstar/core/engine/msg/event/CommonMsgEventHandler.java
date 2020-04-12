package tech.xuanwu.northstar.core.engine.msg.event;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.Message;
import tech.xuanwu.northstar.entity.StrategyInfo;

/**
 * 
 * @author kevinhuangwl
 *
 */
@Slf4j
@Component
public class CommonMsgEventHandler {

	private ConcurrentHashMap<UUID, List<String>> roomMap = new ConcurrentHashMap<>();
	
	@OnConnect  
    private void onConnect(final SocketIOClient client) {
    	log.info("【策略连接】-[{}],已连接", client.getSessionId());
    }  
  
    @OnDisconnect  
    private void onDisconnect(final SocketIOClient client) {
    	log.info("【策略断开】-[{}],断开连接", client.getSessionId());
    	
    	clearOutRooms(client);
    }
    
    private void clearOutRooms(SocketIOClient client) {
    	List<String> roomList = roomMap.remove(client.getSessionId());
    	for(String room : roomList) {
    		client.leaveRoom(room);
    	}
    	log.info("【清理房间】-[{}] 离开房间{}", client.getSessionId(), JSON.toJSONString(roomList));
    }
    
    @OnEvent(Message.REG_STRATEGY)
    private void onRegisterStrategy(final SocketIOClient client, StrategyInfo s) throws Exception {
    	
    	log.info("【策略注册】-[{}],【{}】绑定账户：{}，订阅合约：{}", 
    			client.getSessionId(), 
    			s.getStrategyName(), 
    			s.getAccountGatewayId(), 
    			JSON.toJSONString(s.getSubscribeContracts()));
    	
    	String[] contractList = s.getSubscribeContracts();
    	
    	List<String> roomList = new ArrayList<String>(contractList.length+1);
    	client.joinRoom(s.getStrategyName());
    	roomList.add(s.getStrategyName());
    	
    	for(String contract : contractList) {
    		client.joinRoom(contract);
    		roomList.add(contract);
    		
    	}
    	
    	roomMap.put(client.getSessionId(), roomList);
    }
    
}
