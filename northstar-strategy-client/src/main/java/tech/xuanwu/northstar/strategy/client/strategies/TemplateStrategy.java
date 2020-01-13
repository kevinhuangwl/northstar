package tech.xuanwu.northstar.strategy.client.strategies;

import tech.xuanwu.northstar.strategy.client.msg.MessageClient;
import xyz.redtorch.pb.CoreField.BarField;
import xyz.redtorch.pb.CoreField.TickField;

public abstract class TemplateStrategy implements TradeStrategy{

	protected boolean isRunning = false;
	
	protected boolean isBlocking = false;
	
	protected String name;
	
	protected MessageClient msgClient;
	
	protected abstract void onTick(TickField tick);
	
	protected abstract void onBar(BarField bar);
	
	/*采用文华风格的开平仓API设计，让策略端避免考虑平今仓还是平旧仓的问题，默认优先平旧仓，由接口平台的风控模块自动计算*/
	
	protected void BK() {}
	
	protected void SK() {}
	
	protected void BP() {}
	
	protected void SP() {}
}
