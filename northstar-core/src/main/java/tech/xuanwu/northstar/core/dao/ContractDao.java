package tech.xuanwu.northstar.core.dao;

import java.util.List;

import xyz.redtorch.pb.CoreField.ContractField;

public interface ContractDao {

	List<ContractField> getSubscribeContracts();
	
	void upsertSubscribeContract(ContractField contract);
}
