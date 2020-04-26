package tech.xuanwu.northstar.strategy.client.msg;

import java.net.URISyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.protobuf.InvalidProtocolBufferException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter.Listener;
import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.Message;
import tech.xuanwu.northstar.entity.StrategyInfo;
import tech.xuanwu.northstar.strategy.client.strategies.TemplateStrategy;
import tech.xuanwu.northstar.strategy.client.strategies.TradeStrategy;
import xyz.redtorch.pb.CoreField.TickField;

/**
 * 通信客户端，每个策略自己维护一个实例
 * @author kevinhuangwl
 *
 */
@Slf4j
public class MessageClient {
	
	Socket client;
	
	TradeStrategy strategy;
	
	public MessageClient(String coreServiceEndpoint, TradeStrategy s){
		try {
			this.client = IO.socket(coreServiceEndpoint);
			this.strategy = s;
		} catch (URISyntaxException e) {
			log.error("通信客户端创建异常", e);
		}
	}
	
	/**
	 * 收到TICK数据
	 * @param tick
	 */
	private void onTick(TickField tick) {
		((TemplateStrategy)strategy).onTickEvent(tick);
	}
	
	
	private <T> JSONObject wrapAsJSON(T t) throws JSONException {
		return new JSONObject(new Gson().toJson(t));
	}
	
	/**
	 * 建立与socket服务端连接
	 */
	public void connect() {
		if(client.connected()) {
			return;
		}
		
		final Listener callback = (data)->{
			String gatewayId = strategy.getGatewayId();
			String accountName = strategy.getAccountName();
			String strategyName = strategy.getStrategyName();
			String[] contractList = strategy.getSubscribeContractList();
			StrategyInfo strategyInfo = new StrategyInfo(gatewayId, accountName, strategyName, contractList);
			
			try {
				client.emit(Message.REG_STRATEGY, wrapAsJSON(strategyInfo));
			} catch (JSONException e) {
				log.error("", e);
			}
		};
		
		client.once(Socket.EVENT_CONNECTING, callback);
		
		client.on(Socket.EVENT_RECONNECTING, callback);
		
		client.on(Message.MARKET_DATA, (data)->{
			byte[] b = (byte[]) data[0];
			try {
				TickField tick = TickField.parseFrom(b);
				onTick(tick);
			} catch (InvalidProtocolBufferException e) {
				log.error("Tick数据转换异常",e);
			}
		});
		
		client.connect();
	}
	
	/**
	 * 断开与socket服务端连接
	 * @throws JSONException 
	 */
	public void disconnect() {
		client.disconnect();
	}
	
}
