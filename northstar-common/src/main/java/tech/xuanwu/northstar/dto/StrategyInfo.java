package tech.xuanwu.northstar.dto;

import lombok.Data;

@Data
public class StrategyInfo {

	String accountName;
	
	String strategyName;
	
	String[] subscribeContracts;
	
	public StrategyInfo() {}
	
	public StrategyInfo(String accountName, String strategyName, String[] subscribeContracts) {
		this.accountName = accountName;
		this.strategyName = strategyName;
		this.subscribeContracts = subscribeContracts;
	}
}
