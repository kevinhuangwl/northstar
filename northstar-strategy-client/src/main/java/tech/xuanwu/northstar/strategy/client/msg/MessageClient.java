package tech.xuanwu.northstar.strategy.client.msg;

import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Value;

import com.google.protobuf.InvalidProtocolBufferException;

import io.socket.client.IO;
import io.socket.client.Socket;
import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.MessageType;
import tech.xuanwu.northstar.strategy.client.strategies.TradeStrategy;
import xyz.redtorch.pb.CoreField.BarField;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.TickField;

/**
 * 通信客户端
 * @author kevinhuangwl
 *
 */
@Slf4j
public class MessageClient {
	
	@Value("${northstar.url}")
	private String coreEngineUrl;
	
	Socket socketClient;
	
	TradeStrategy strategy;
	
	MessageClient() throws URISyntaxException{
		Socket socket = IO.socket(coreEngineUrl);
		
		socket.on(MessageType.MARKET_TICK_DATA.toString(), (data)->{
			byte[] b = (byte[]) data[0];
			try {
				TickField tick = TickField.parseFrom(b);
				onTick(tick);
			} catch (InvalidProtocolBufferException e) {
				log.error("Tick数据转换异常",e);
			}
		});
		
		socket.connect();
		socketClient = socket;
	}
	
	public static MessageClient getInstance() throws URISyntaxException {
		
		
		return null ;
	}
	
	
	public void onTick(TickField tick) {
		
	}
	
	public void onBar(BarField bar) {
		
	}

	/**
	 * 发送委托单
	 * @param order
	 */
	public void sendOrder(OrderField order) {
		
	}
	
	/**
	 * 注册策略
	 */
	public void registerStrategy() {
		
	}
	
	/**
	 * 订阅合约
	 */
	public void subscribeContracts() {
		
	}
	
}
