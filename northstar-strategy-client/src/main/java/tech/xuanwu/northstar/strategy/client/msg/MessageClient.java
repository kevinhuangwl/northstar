package tech.xuanwu.northstar.strategy.client.msg;

import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.protobuf.InvalidProtocolBufferException;

import io.socket.client.IO;
import io.socket.client.Socket;
import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.MessageType;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.TickField;

/**
 * 通信客户端
 * @author kevinhuangwl
 *
 */
@Slf4j
@Component
public class MessageClient {
	
	@Value("${northstar.url}")
	private String coreEngineUrl;
	
	public void init() throws URISyntaxException {
		Socket socket = IO.socket(coreEngineUrl);
		
		socket.on(MessageType.CONTRACT_LIST.toString(),(data)->{
			log.info("收到：{}",(int)data[0]);
		});
		
		socket.on(MessageType.MARKET_DATA.toString(), (data)->{
			byte[] b = (byte[]) data[0];
			try {
				TickField tick = TickField.parseFrom(b);
				log.info(tick.toString());
			} catch (InvalidProtocolBufferException e) {
				e.printStackTrace();
			}
		});
		
		socket.connect();
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
