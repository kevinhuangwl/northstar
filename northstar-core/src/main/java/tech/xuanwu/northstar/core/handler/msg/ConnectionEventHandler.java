package tech.xuanwu.northstar.core.handler.msg;

import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.MessageType;

@Slf4j
@Component
public class ConnectionEventHandler {

	@OnConnect  
    public void onConnect(final SocketIOClient client) {
    	log.info("【连接】-[{}],已连接", client.getSessionId());
    	for(var i=0; i<10; i++) {
    		client.sendEvent(MessageType.CONTRACT_LIST.toString(), i);
    	}
    }  
  
    @OnDisconnect  
    public void onDisconnect(final SocketIOClient client) {
    	log.info("【断开】-[{}],断开连接", client.getSessionId());
    }
    
//    @OnEvent("Login")
//    public void loginRoom(final SocketIOClient client, final UserReq params) {
//    	String room = params.getRoom();
//    	
//    	client.joinRoom(room);
////    	log.info("【加入房间】-[{}],【{}】加入房间【{}】", role, room);
//    	
//    }
//    
//    @OnEvent("Logout")
//    public void logoutRoom(final SocketIOClient client, final UserReq params) {
//    	String room = params.getRoom();
//    	
//    	client.leaveRoom(room);
////    	log.info("【离开房间】-[{}],【{}】离开房间【{}】", clientID, role, room);
//    	
//    }
}
