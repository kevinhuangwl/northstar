package tech.xuanwu.northstar.entity;

import lombok.Data;

@Data
public class StrategyInfo {

	String accountGatewayId;
	
	String strategyName;
	
	String[] subscribeContracts;
	
	public StrategyInfo() {}
	
	public StrategyInfo(String accountGatewayId, String strategyName, String[] subscribeContracts) {
		this.accountGatewayId = accountGatewayId;
		this.strategyName = strategyName;
		this.subscribeContracts = subscribeContracts;
	}
}
