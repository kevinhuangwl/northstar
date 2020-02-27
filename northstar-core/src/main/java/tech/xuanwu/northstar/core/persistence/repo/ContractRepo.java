package tech.xuanwu.northstar.core.persistence.repo;

import java.util.List;

import tech.xuanwu.northstar.entity.ContractInfo;

public interface ContractRepo {

	boolean upsert(ContractInfo contract) throws Exception;
	
	List<ContractInfo> getAllSubscribedContracts() throws Exception;
}
