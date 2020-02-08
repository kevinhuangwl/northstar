package tech.xuanwu.northstar.strategy.client.strategies;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import xyz.redtorch.pb.CoreField.BarField;
import xyz.redtorch.pb.CoreField.TickField;

@Slf4j
@Component
@ConfigurationProperties(prefix="strategy-setting.demo-strategy")
public class DemoStrategy extends TemplateStrategy {
	

	@Override
	protected void onTick(TickField tick) {
		log.info("合约-[{}], 价：{}，仓：{}，量：{}", 
				tick.getContract().getSymbol(), 
				tick.getLastPrice(), 
				tick.getOpenInterestChange(), 
				tick.getVolumeChange());
	}

	@Override
	protected void onBar(BarField bar) {
		log.info("合约-[{}]，开：{}，高：{}，低：{}，收：{}，仓：{}，量：{}",
				bar.getContract().getContractId(),
				bar.getOpenPrice(),
				bar.getHighPrice(),
				bar.getLowPrice(),
				bar.getClosePrice(),
				bar.getOpenInterest(),
				bar.getVolume());

	}
	
}
