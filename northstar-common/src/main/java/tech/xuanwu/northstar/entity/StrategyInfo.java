package tech.xuanwu.northstar.entity;

import lombok.Data;

@Data
public class StrategyInfo {

	String gatewayId;
	
	String accountName;
	
	String strategyName;
	
	String[] subscribeContracts;
	
	public StrategyInfo() {}
	
	public StrategyInfo(String gatewayId, String accountName, String strategyName, String[] subscribeContracts) {
		this.gatewayId = gatewayId;
		this.accountName = accountName;
		this.strategyName = strategyName;
		this.subscribeContracts = subscribeContracts;
	}
}
