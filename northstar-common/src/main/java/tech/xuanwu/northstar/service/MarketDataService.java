package tech.xuanwu.northstar.service;

import java.util.List;

import tech.xuanwu.northstar.entity.ContractInfo;

/**
 * 行情服务
 * @author kevinhuangwl
 *
 */
public interface MarketDataService {

	/**
	 * 订阅合约
	 * @param gatewayId
	 * @param contractName
	 * @return
	 * @throws Exception
	 */
	boolean subscribeContract(String gatewayId, String contractName) throws Exception;
	
//	List<ContractInfo> getAllSubscribedContracts(String gatewayId) throws Exception;
	
	List<ContractInfo> getAvailableContracts(String gatewayId) throws Exception;
}
