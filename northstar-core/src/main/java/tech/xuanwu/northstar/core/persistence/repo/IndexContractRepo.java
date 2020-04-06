package tech.xuanwu.northstar.core.persistence.repo;

import java.util.List;

import tech.xuanwu.northstar.entity.ContractInfo;

public interface IndexContractRepo {
	
	/**
	 * 以contractId作为唯一标识判断，不存在时插入
	 * @param contract
	 * @return
	 * @throws Exception
	 */
	boolean insertIfAbsent(ContractInfo contract) throws Exception;
	
	/**
	 * 删除
	 * @param contract
	 * @return
	 */
	boolean delete(String gatewayId, String symbol);

	/**
	 * 获取所有订阅的指数合约
	 * @param gatewayId
	 * @return
	 * @throws Exception
	 */
	List<ContractInfo> getAllSubscribedContracts(String gatewayId) throws Exception;
}
