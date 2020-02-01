package tech.xuanwu.northstar.core.engine.msg.event;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.MessageType;
import tech.xuanwu.northstar.core.util.ContractMap;
import tech.xuanwu.northstar.gateway.GatewayApi;
import xyz.redtorch.pb.CoreField.ContractField;

@Slf4j
@Component
public class CommonMsgEventHandler {

	ConcurrentHashMap<UUID, List<String>> roomMap = new ConcurrentHashMap<>();
	
	@Autowired
	GatewayApi ctpGatewayApi;
	
	@Autowired
	ContractMap globeContractMap;
	
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
    
    @OnEvent(MessageType.REG_STRATEGY)
    private void registerStrategy(final SocketIOClient client, String strategyName, String[] contractList) {
    	
    	log.info("【策略注册】-[{}],【{}】订阅合约{}", client.getSessionId(), strategyName, JSON.toJSONString(contractList));
    	
    	List<String> roomList = new ArrayList<String>(contractList.length+1);
    	client.joinRoom(strategyName);
    	roomList.add(strategyName);
    	for(String contract : contractList) {
    		client.joinRoom(contract);
    		roomList.add(contract);
    		
    		//订阅合约
    		subscribeContract(contract);
    	}
    	
    	roomMap.put(client.getSessionId(), roomList);
    }
    
    private void subscribeContract(String contract) {
    	ContractField c = globeContractMap.getContractBySymbol(contract);
    	if(c != null) {
    		ctpGatewayApi.subscribe(c);
    	}else {
    		log.warn("合约 [{}] 可能是一个非法合约，在交易行情接口中查询不到");
    	}
    }
    
    @OnEvent(MessageType.PLACE_ORDER)
    private void placeOrder(final SocketIOClient client) {
    	
    }
    
    @OnEvent(MessageType.CANCEL_ORDER)
    private void cancelOrder(final SocketIOClient client) {
    	
    }
}
