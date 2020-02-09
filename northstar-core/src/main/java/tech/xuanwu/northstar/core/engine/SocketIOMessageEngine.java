package tech.xuanwu.northstar.core.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.SocketIOServer;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.MessageType;
import tech.xuanwu.northstar.engine.MessageEngine;
import xyz.redtorch.pb.CoreField.AccountField;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.TickField;
import xyz.redtorch.pb.CoreField.TradeField;

@Slf4j
@Component
public class SocketIOMessageEngine implements MessageEngine{
	
	@Autowired
	SocketIOServer server;
	
	@Override
	public void emitTick(TickField tick) {
		//以合约名称作为广播的房间号
		String symbol = tick.getUnifiedSymbol();
		
		server.getRoomOperations(symbol).sendEvent(MessageType.MARKET_TICK_DATA, tick.toByteArray());
		log.info("收到{}数据", symbol);
	}
	
	@Override
	public void emitAccount(AccountField account) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void emitOrder(OrderField order) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void emitTransaction(TradeField trade) {
		// TODO Auto-generated method stub
		
	}
	
	
    


}
