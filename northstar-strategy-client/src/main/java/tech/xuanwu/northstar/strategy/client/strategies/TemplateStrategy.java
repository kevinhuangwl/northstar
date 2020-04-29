package tech.xuanwu.northstar.strategy.client.strategies;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.CommonConstant;
import tech.xuanwu.northstar.service.AccountService;
import tech.xuanwu.northstar.service.MailSenderService;
import tech.xuanwu.northstar.service.TradeService;
import tech.xuanwu.northstar.strategy.client.config.BaseStrategyConfig;
import tech.xuanwu.northstar.strategy.client.constant.TradeState;
import tech.xuanwu.northstar.strategy.client.msg.MessageClient;
import xyz.redtorch.common.util.bar.BarGenerator;
import xyz.redtorch.common.util.bar.CommonBarCallBack;
import xyz.redtorch.pb.CoreEnum.DirectionEnum;
import xyz.redtorch.pb.CoreEnum.OffsetFlagEnum;
import xyz.redtorch.pb.CoreField.BarField;
import xyz.redtorch.pb.CoreField.TickField;

@Slf4j
public abstract class TemplateStrategy implements TradeStrategy, InitializingBean, DisposableBean{
	
	protected BaseStrategyConfig strategyConfig;

	/*策略运行状态*/
	protected volatile boolean running = false;
	
	/*策略交易状态*/
	protected TradeState tradeState = TradeState.EMPTY_POSITION;
	
	@Value("${northstar.message.endpoint}")
	protected String messageEndpoint;
	
	protected MessageClient msgClient;
	
	private ConcurrentHashMap<String, BarGenerator> barGeneratorMap = new ConcurrentHashMap<String, BarGenerator>();
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private TradeService tradeService;
	
	@Autowired
	private MailSenderService mailService;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		//每个策略绑定一个消息客户端用于与行情交易平台通信
		msgClient = new MessageClient(messageEndpoint, this);
		msgClient.connect();
		
		System.out.println("#################################");
		System.out.println(String.format("策略名称：%s", strategyConfig.getStrategyName()));
		System.out.println(String.format("交易账户名称：%s", strategyConfig.getAccountId()));
		System.out.println(String.format("订阅合约名称：%s", JSON.toJSONString(strategyConfig.getMdContracts())));
		System.out.println(String.format("交易合约名称：%s", JSON.toJSONString(strategyConfig.getTdContracts())));
		System.out.println("#################################");
		
		//连接账户
		accountService.connect(strategyConfig.getAccountId());
	}
	
	@Override
	public void destroy() throws Exception {
		log.info("断开策略-[{}]", strategyConfig.getStrategyName());
		msgClient.disconnect();
	}
	
	@Override
	public String getGatewayId() {
		return strategyConfig.getGatewayId();
	}
	
	@Override
	public String getAccountId() {
		return strategyConfig.getAccountId();
	}
	
	@Override
	public String getStrategyName() {
		return strategyConfig.getStrategyName();
	}
	
	@Override
	public String[] getSubscribeContractList() {
		return strategyConfig.getMdContracts();
	}
	
	@Override
	public void resume() {
		running = true;
	}
	
	@Override
	public void suspend() {
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
	
	/*让策略端避免考虑平今仓还是平旧仓的问题，默认优先平旧仓，由接口平台的风控模块自动计算*/
	protected void buyOpen(String symbol, int volume, double price) {
		open(symbol, volume, price, DirectionEnum.D_Buy);
	}
	
	protected void sellOpen(String symbol, int volume, double price) {
		open(symbol, volume, price, DirectionEnum.D_Sell);
	}
	
	private void open(String symbol, int volume, double price, DirectionEnum dir) {
		if(tradeState != TradeState.EMPTY_POSITION) {
			return;
		}
		try {
			String originOrderId = tradeService.submitOrder(strategyConfig.getAccountId(),
					symbol, price, volume, dir, OffsetFlagEnum.OF_Open);
			
			onOpening(originOrderId);
			
		} catch (Exception e) {
			String errMsg = String.format("【开仓异常】账户 [%s] 合约 [%s] 开仓异常。", strategyConfig.getAccountId(), symbol);
			log.error(errMsg, e);
			try {
				mailService.sendMessage(errMsg, LocalDateTime.now().format(CommonConstant.DT_FORMAT_FORMATTER));
			} catch (Exception ex) {
				log.error("", e);
			}
			throw new Error();
		}
	}
	
	protected void buyClose(String symbol, int volume, double price) {
		close(symbol, volume, price, DirectionEnum.D_Buy);
	}
	
	protected void sellClose(String symbol, int volume, double price) {
		close(symbol, volume, price, DirectionEnum.D_Sell);
	}
	
	private void close(String symbol, int volume, double price, DirectionEnum dir) {
		if(tradeState != TradeState.LONG_POSITION || tradeState != TradeState.SHORT_POSITION) {
			return;
		}
		try {
			String originOrderId = tradeService.submitOrder(strategyConfig.getAccountId(),
					symbol, price, volume, dir, OffsetFlagEnum.OF_Close);
			
			onClosing(originOrderId);
			
		} catch (Exception e) {
			String errMsg = String.format("【平仓异常】账户 [%s] 合约 [%s] 平仓异常。", strategyConfig.getAccountId(), symbol);
			log.error(errMsg, e);
			try {
				mailService.sendMessage(errMsg, LocalDateTime.now().format(CommonConstant.DT_FORMAT_FORMATTER));
			} catch (Exception ex) {
				log.error("", e);
			}
			throw new Error();
		}
	}
	
	private void onOpening(String originOrderId) {
		tradeState = TradeState.OPENNING_POSITION;
	}
	
	private void onClosing(String originOrderId) {
		tradeState = TradeState.CLOSING_POSITION;
	}
	
	//TODO 难点：下单之后，如何拿到订单号，如何制定撤单策略，如何反馈成交
	
	
	
}
