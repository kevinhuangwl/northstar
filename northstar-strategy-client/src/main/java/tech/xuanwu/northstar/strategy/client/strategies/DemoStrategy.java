package tech.xuanwu.northstar.strategy.client.strategies;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import xyz.redtorch.pb.CoreField.BarField;
import xyz.redtorch.pb.CoreField.TickField;

@Component
@ConfigurationProperties(prefix="strategy-setting.demo-strategy")
public class DemoStrategy extends TemplateStrategy {
	

	@Override
	protected void onTick(TickField tick) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onBar(BarField bar) {
		// TODO Auto-generated method stub

	}
	
}
