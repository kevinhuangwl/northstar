package tech.xuanwu.northstar.core.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.SocketIOServer;

import tech.xuanwu.northstar.constant.MessageType;
import tech.xuanwu.northstar.engine.MessageEngine;
import xyz.redtorch.pb.CoreField.TickField;

@Component
public class SocketIOMessageEngine implements MessageEngine{
	
	@Autowired
	SocketIOServer server;

	@Override
	public void emitTick(TickField tick) {
		//以合约名称作为广播的房间号
		String symbol = tick.getContract().getSymbol();
		
		server.getRoomOperations(symbol).sendEvent(MessageType.MARKET_DATA.toString(), tick.toByteArray());
	}

}
