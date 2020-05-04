package tech.xuanwu.northstar.strategy.client.trade.strategy;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.CommonConstant;
import tech.xuanwu.northstar.service.AccountService;
import tech.xuanwu.northstar.service.MailSenderService;
import tech.xuanwu.northstar.service.TradeService;
import tech.xuanwu.northstar.strategy.client.config.common.DefaultSubmitOrderConfig;
import tech.xuanwu.northstar.strategy.client.config.strategy.BaseStrategyConfig;
import tech.xuanwu.northstar.strategy.client.constant.TradeState;
import tech.xuanwu.northstar.strategy.client.msg.MessageClient;
import xyz.redtorch.common.util.bar.BarGenerator;
import xyz.redtorch.common.util.bar.CommonBarCallBack;
import xyz.redtorch.pb.CoreEnum.DirectionEnum;
import xyz.redtorch.pb.CoreEnum.OffsetFlagEnum;
import xyz.redtorch.pb.CoreField.BarField;
import xyz.redtorch.pb.CoreField.TickField;

@Slf4j
public abstract class TemplateStrategy implements Strategy, InitializingBean{
	
	protected BaseStrategyConfig strategyConfig;
	
	@Autowired
	protected DefaultSubmitOrderConfig defaultOrderConfig;

	/*策略运行状态*/
	protected volatile boolean running = false;
	
	/*策略交易状态*/
	protected TradeState tradeState = TradeState.EMPTY_POSITION;
	
	@Value("${northstar.message.endpoint}")
	protected String messageEndpoint;
	
	private ConcurrentHashMap<String, BarGenerator> barGeneratorMap = new ConcurrentHashMap<String, BarGenerator>();
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private TradeService tradeService;
	
	@Autowired
	private MailSenderService mailService;
	
	@Autowired
	private MessageClient msgClient;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("#################################");
		System.out.println(String.format("策略名称：%s", strategyConfig.getStrategyName()));
		System.out.println(String.format("交易账户名称：%s", strategyConfig.getAccountId()));
//		System.out.println(String.format("订阅合约名称：%s", JSON.toJSONString(strategyConfig.getMdContracts())));
//		System.out.println(String.format("交易合约名称：%s", JSON.toJSONString(strategyConfig.getTdContracts())));
		System.out.println("#################################");
		
		//注册策略
		msgClient.registerStrategy(this);
		//连接账户
		accountService.connect(strategyConfig.getAccountId());
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
	
	CommonBarCallBack barCallback = (barField)->{
		onBar(barField);
	};
	
	@Override
	public void updateTick(TickField tick) {
		//优先传入onTick计算策略
		onTick(tick);
		
		//再计算Bar
		String contractId = tick.getUnifiedSymbol();
		if(!barGeneratorMap.containsKey(contractId)) {
			barGeneratorMap.put(contractId, new BarGenerator(barCallback)); 
		}
		
		barGeneratorMap.get(contractId).updateTick(tick);
	}
	
	
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
			String originOrderId = tradeService.submitOrder(
					strategyConfig.getAccountId(),
					symbol, 
					price, 
					0D,
					volume,
					defaultOrderConfig.getOrderPriceType(),
					dir,
					OffsetFlagEnum.OF_Open,
					defaultOrderConfig.getHedgeFlag(),
					defaultOrderConfig.getTimeCondition(),
					defaultOrderConfig.getVolumeCondition(),
					defaultOrderConfig.getTrigerCondition());
			
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
			String originOrderId = tradeService.submitOrder(
					strategyConfig.getAccountId(),
					symbol,
					price,
					0D,
					volume,
					defaultOrderConfig.getOrderPriceType(),
					dir, 
					OffsetFlagEnum.OF_Close,
					defaultOrderConfig.getHedgeFlag(),
					defaultOrderConfig.getTimeCondition(),
					defaultOrderConfig.getVolumeCondition(),
					defaultOrderConfig.getTrigerCondition());
			
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
	
	
}
