package tech.xuanwu.northstar.core.engine.msg.event;

import java.util.ArrayList;
import java.util.EventObject;
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
import com.google.protobuf.InvalidProtocolBufferException;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.EventType;
import tech.xuanwu.northstar.constant.MessageType;
import tech.xuanwu.northstar.core.util.FutureDictionary;
import tech.xuanwu.northstar.dto.StrategyInfo;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.gateway.GatewayApi;
import xyz.redtorch.pb.CoreField.CancelOrderReqField;
import xyz.redtorch.pb.CoreField.ContractField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;

@Slf4j
@Component
public class CommonMsgEventHandler {

	ConcurrentHashMap<UUID, List<String>> roomMap = new ConcurrentHashMap<>();
	
	@Autowired
	GatewayApi ctpGatewayApi;
	
	@Autowired
	FutureDictionary globeContractMap;
	
	@Autowired
	RuntimeEngine rtEngine;
	
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
    private void onRegisterStrategy(final SocketIOClient client, StrategyInfo s) {
    	
    	log.info("【策略注册】-[{}],【{}】绑定账户：{}，订阅合约：{}", 
    			client.getSessionId(), 
    			s.getStrategyName(), 
    			s.getAccountName(), 
    			JSON.toJSONString(s.getSubscribeContracts()));
    	
    	String[] contractList = s.getSubscribeContracts();
    	
    	List<String> roomList = new ArrayList<String>(contractList.length+1);
    	client.joinRoom(s.getStrategyName());
    	roomList.add(s.getStrategyName());
    	
    	rtEngine.emitEvent(EventType.REGISTER_STRATEGY.toString(), new EventObject(s));
    	
    	for(String contract : contractList) {
    		client.joinRoom(contract);
    		roomList.add(contract);
    		
    		//订阅合约
    		ContractField c = globeContractMap.getContractByName(contract);
    		subscribeContract(c);
    		
    		rtEngine.emitEvent(EventType.REGISTER_CONTRACT.toString(), new EventObject(c));
    	}
    	
    	roomMap.put(client.getSessionId(), roomList);
    }
    
    private void subscribeContract(ContractField c) {
    	if(c != null) {
    		ctpGatewayApi.subscribe(c);
    	}else {
    		log.warn("合约 [{}] 可能是一个非法合约，在交易行情接口中查询不到");
    	}
    }
    
    @OnEvent(MessageType.PLACE_ORDER)
    private void onPlaceOrder(final SocketIOClient client, byte[] data) {
    	try {
			SubmitOrderReqField submitOrderReq = SubmitOrderReqField.parseFrom(data);
			rtEngine.emitEvent(EventType.PLACE_ORDER.toString(), new EventObject(submitOrderReq));
		} catch (InvalidProtocolBufferException e) {
			log.error("", e);
		}
    }
    
    @OnEvent(MessageType.WITHDRAW_ORDER)
    private void onCancelOrder(final SocketIOClient client, byte[] data) {
    	try {
			CancelOrderReqField cancelOrderReq = CancelOrderReqField.parseFrom(data);
			rtEngine.emitEvent(EventType.WITHDRAW_ORDER.toString(), new EventObject(cancelOrderReq));
		} catch (InvalidProtocolBufferException e) {
			log.error("", e);
		}
    }
}
