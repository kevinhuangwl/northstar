package tech.xuanwu.northstar.core.handler.msg;

import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ConnectionEventHandler {

	@OnConnect  
    public void onConnect(final SocketIOClient client) {
    	log.info("【连接】-[{}],已连接", client.getSessionId());
    }  
  
    @OnDisconnect  
    public void onDisconnect(final SocketIOClient client) {
    	log.info("【断开】-[{}],断开连接", client.getSessionId());
    }
    
}
