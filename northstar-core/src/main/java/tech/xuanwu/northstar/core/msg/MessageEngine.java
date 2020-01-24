package tech.xuanwu.northstar.core.msg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.SocketIOServer;

import tech.xuanwu.northstar.constant.MessageType;
import xyz.redtorch.pb.CoreField.TickField;

@Component
public class MessageEngine {
	
	@Autowired
	SocketIOServer server;

	public void emitTick(TickField tick) {
		//以合约名称作为广播的房间号
		String symbol = tick.getContract().getSymbol();
		
		server.getRoomOperations(symbol).sendEvent(MessageType.MARKET_TICK_DATA.toString(), tick.toByteArray());
	}

}
