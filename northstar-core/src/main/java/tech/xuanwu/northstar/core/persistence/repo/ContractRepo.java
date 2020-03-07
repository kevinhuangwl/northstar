package tech.xuanwu.northstar.core.persistence.repo;

import java.util.List;

import tech.xuanwu.northstar.entity.ContractInfo;

public interface ContractRepo {
	
	boolean upsert(ContractInfo contract) throws Exception;

	boolean insertIfAbsent(ContractInfo contract) throws Exception;
	
	boolean delete(ContractInfo contract);
	
	ContractInfo getContractBySymbol(String symbol) throws Exception;
	
	List<ContractInfo> getAllSubscribedContracts() throws Exception;
	
	List<ContractInfo> getAllAvailableContracts() throws Exception;
}
