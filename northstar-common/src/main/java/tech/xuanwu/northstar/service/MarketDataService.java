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
	
	/**
	 * 获取期货合约
	 * @param gatewayId
	 * @return
	 * @throws Exception
	 */
	List<ContractInfo> getAvailableFutureContracts(String gatewayId) throws Exception;
}
