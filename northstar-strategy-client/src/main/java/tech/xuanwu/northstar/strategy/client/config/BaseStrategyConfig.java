package tech.xuanwu.northstar.strategy.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="strategy-setting.demo-strategy")
public class BaseStrategyConfig {

	/*账户名称*/
	protected String accountName;
	
	/*策略名称*/
	protected String strategyName;
	
	/*订阅行情合约*/
	protected String[] mdContracts;
	
	/*下单目标合约*/
	protected String[] tdContracts;

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getStrategyName() {
		return strategyName;
	}

	public void setStrategyName(String strategyName) {
		this.strategyName = strategyName;
	}

	public String[] getMdContracts() {
		return mdContracts;
	}

	public void setMdContracts(String[] mdContracts) {
		this.mdContracts = mdContracts;
	}

	public String[] getTdContracts() {
		return tdContracts;
	}

	public void setTdContracts(String[] tdContracts) {
		this.tdContracts = tdContracts;
	}
	
}
