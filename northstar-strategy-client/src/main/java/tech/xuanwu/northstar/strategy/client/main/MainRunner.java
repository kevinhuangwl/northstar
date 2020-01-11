package tech.xuanwu.northstar.strategy.client.main;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.google.protobuf.InvalidProtocolBufferException;

import io.socket.client.IO;
import io.socket.client.Socket;
import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.MessageType;
import xyz.redtorch.pb.CoreField.TickField;

@Slf4j
@Component
public class MainRunner implements CommandLineRunner{

	@Override
	public void run(String... args) throws Exception {
		Socket socket = IO.socket("http://localhost:51666");
		
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
		
		for(;;) {
			Thread.sleep(Integer.MAX_VALUE);
		}
	}

}
