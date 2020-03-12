package tech.xuanwu.northstar.core.persistence.repo;

import java.util.List;

import tech.xuanwu.northstar.entity.ContractInfo;

public interface ContractRepo {
	
	/**
	 * 更新
	 * @param contract
	 * @return
	 * @throws Exception
	 */
	boolean updateById(ContractInfo contract) throws Exception;
	
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
	boolean delete(String unifiedSymbol);
	
	/**
	 * 依据合约名称查询合约信息
	 * @param unifiedSymbol
	 * @return
	 * @throws Exception
	 */
	ContractInfo getContractBySymbol(String gatewayId, String symbol) throws Exception;
	
	/**
	 * 获取所有订阅合约
	 * @return
	 * @throws Exception
	 */
	List<ContractInfo> getAllSubscribedContracts(String gatewayId) throws Exception;
	
	/**
	 * 获取所有有效合约（最后交易日大于当天的合约）
	 * @return
	 * @throws Exception
	 */
	List<ContractInfo> getAllAvailableContracts(String gatewayId) throws Exception;
}
