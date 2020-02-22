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
import tech.xuanwu.northstar.constant.MessageType;
import tech.xuanwu.northstar.entity.StrategyInfo;
import tech.xuanwu.northstar.strategy.client.strategies.TemplateStrategy;
import tech.xuanwu.northstar.strategy.client.strategies.TradeStrategy;
import xyz.redtorch.pb.CoreField.CancelOrderReqField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;
import xyz.redtorch.pb.CoreField.TickField;

/**
 * 通信客户端
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
	
	/**
	 * 发送委托单
	 * @param submitOrderReq
	 */
	public void sendOrder(SubmitOrderReqField submitOrderReq) {
		client.emit(MessageType.SUBMIT_ORDER, submitOrderReq.toByteArray());
	}
	
	/**
	 * 撤销委托单
	 * @param cancelOrderReq
	 */
	public void cancelOrder(CancelOrderReqField cancelOrderReq) {
		client.emit(MessageType.CANCEL_ORDER, cancelOrderReq.toByteArray());
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
			String accountName = strategy.getAccountName();
			String strategyName = strategy.getStrategyName();
			String[] contractList = strategy.getSubscribeContractList();
			StrategyInfo strategyInfo = new StrategyInfo(accountName, strategyName, contractList);
			
			try {
				client.emit(MessageType.REG_STRATEGY, wrapAsJSON(strategyInfo));
			} catch (JSONException e) {
				log.error("", e);
			}
		};
		
		client.once(Socket.EVENT_CONNECTING, callback);
		
		client.on(Socket.EVENT_RECONNECTING, callback);
		
		client.on(MessageType.MARKET_TICK_DATA, (data)->{
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
		String accountName = strategy.getAccountName();
		String strategyName = strategy.getStrategyName();
		String[] contractList = strategy.getSubscribeContractList();
		StrategyInfo strategyInfo = new StrategyInfo(accountName, strategyName, contractList);
		Object[] params = new Object[1];
		try {			
			params[0] = wrapAsJSON(strategyInfo);
			client.emit(MessageType.UNREG_STRATEGY.toString(), params, (data)->{
				client.disconnect();
			});
		} catch (JSONException e) {
			log.error("", e);
		}
	}
	
}
