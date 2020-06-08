package tech.xuanwu.northstar.core.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.Message;
import tech.xuanwu.northstar.engine.MessageEngine;
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
		server.getRoomOperations(tick.getUnifiedSymbol()).sendEvent(Message.MARKET_DATA, tick.toByteArray());
	}
	
	
	/**************************************************/
	/*					消息接收端						  */
	/**************************************************/
	@OnConnect  
    private void onConnect(final SocketIOClient client) {
    	log.info("【客户端连接】-[{}],建立连接", client.getSessionId());
    }  
  
    @OnDisconnect  
    private void onDisconnect(final SocketIOClient client) {
    	log.info("【客户端断开】-[{}],断开连接", client.getSessionId());
    }
    
    @OnEvent("login")
    private void login(final SocketIOClient client, String room) {
    	log.info("【登陆房间】-[{}]加入房间{}", client.getSessionId(), room);
    	client.joinRoom(room);
    }
    
    @OnEvent("logout")
    private void logout(final SocketIOClient client, String room) {
    	log.info("【离开房间】-[{}]离开房间{}", client.getSessionId(), room);
    	client.leaveRoom(room);
    }
}
