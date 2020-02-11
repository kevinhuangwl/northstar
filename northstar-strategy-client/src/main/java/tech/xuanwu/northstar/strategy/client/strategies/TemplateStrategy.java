package tech.xuanwu.northstar.strategy.client.strategies;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSON;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.strategy.client.msg.MessageClient;
import xyz.redtorch.common.util.bar.BarGenerator;
import xyz.redtorch.common.util.bar.CommonBarCallBack;
import xyz.redtorch.pb.CoreField.BarField;
import xyz.redtorch.pb.CoreField.TickField;

@Slf4j
public abstract class TemplateStrategy implements TradeStrategy, InitializingBean{

	protected boolean running = false;
	
	protected boolean isBlocking = false;
	
	@Setter @Getter
	protected String accountName;
	
	@Setter @Getter
	protected String name;
	
	/*订阅行情合约*/
	@Setter @Getter
	protected String[] mdContracts;
	
	/*下单目标合约*/
	@Setter @Getter
	protected String[] tdContracts;
	
	@Value("${northstar.endpoint}")
	protected String coreServiceEndpoint;
	
	protected MessageClient msgClient;
	
	private ConcurrentHashMap<String, BarGenerator> barGeneratorMap = new ConcurrentHashMap<String, BarGenerator>();
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		//每个策略绑定一个消息客户端用于与行情交易平台通信
		msgClient = new MessageClient(coreServiceEndpoint, this);
		msgClient.connect();
		
		init();
	}
	
	@PreDestroy
	protected void terminate() {
		log.info("断开策略-[{}]", name);
		msgClient.disconnect();
	}

	protected void init() {
		System.out.println("#################################");
		System.out.println(String.format("策略名称：%s", name));
		System.out.println(String.format("订阅合约名称：%s", JSON.toJSONString(mdContracts)));
		System.out.println(String.format("交易合约名称：%s", JSON.toJSONString(tdContracts)));
		System.out.println("#################################");
	}
	
	@Override
	public String getStrategyName() {
		return name;
	}
	
	@Override
	public String[] getSubscribeContractList() {
		return mdContracts;
	}
	
	@Override
	public synchronized void resume() {
		running = true;
	}
	
	@Override
	public synchronized void suspend() {
		running = false;
	}
	
	@Override
	public boolean isRunning() {
		return running;
	}
	
	
	public void onTickEvent(TickField tick) {
		//优先传入onTick计算策略
		onTick(tick);
		
		//再计算Bar
		String contractId = tick.getUnifiedSymbol();
		if(!barGeneratorMap.containsKey(contractId)) {
			barGeneratorMap.put(contractId, new BarGenerator(barCallback)); 
		}
		
		barGeneratorMap.get(contractId).updateTick(tick);
	}
	
	CommonBarCallBack barCallback = (barField)->{
		onBar(barField);
	};
	
	
	protected abstract void onTick(TickField tick);
	
	protected abstract void onBar(BarField bar);
	
	/*采用文华风格的开平仓API设计，让策略端避免考虑平今仓还是平旧仓的问题，默认优先平旧仓，由接口平台的风控模块自动计算*/
	protected void BK(String symbol, int volume) {}
	
	protected void SK(String symbol, int volume) {}
	
	protected void BP(String symbol, int volume) {}
	
	protected void SP(String symbol, int volume) {}
	
	//TODO 难点：下单之后，如何拿到订单号，如何制定撤单策略，如何反馈成交
	
	
	
}
