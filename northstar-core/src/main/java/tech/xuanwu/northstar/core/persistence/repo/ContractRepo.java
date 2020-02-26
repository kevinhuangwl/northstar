package tech.xuanwu.northstar.core.persistence.repo;

import java.util.List;

import xyz.redtorch.pb.CoreField.ContractField;

public interface ContractRepo {

	boolean upsert(ContractField contract) throws Exception;
	
	List<ContractField> getAllSubscribeContracts() throws Exception;
}
