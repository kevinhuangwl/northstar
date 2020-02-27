package tech.xuanwu.northstar.core.service;

import java.util.List;

import tech.xuanwu.northstar.entity.ContractInfo;

public interface MarketDataService {

	
	boolean subscribeContract(String gatewayId, String contractName) throws Exception;
	
	List<ContractInfo> getAllSubscribedContracts() throws Exception;
	
}
