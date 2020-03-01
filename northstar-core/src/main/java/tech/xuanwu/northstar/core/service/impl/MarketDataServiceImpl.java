package tech.xuanwu.northstar.core.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.xuanwu.northstar.core.persistence.repo.ContractRepo;
import tech.xuanwu.northstar.core.service.MarketDataService;
import tech.xuanwu.northstar.core.util.FutureDictionary;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.entity.ContractInfo;
import tech.xuanwu.northstar.exception.NoSuchContractException;
import xyz.redtorch.pb.CoreField.ContractField;

@Service
public class MarketDataServiceImpl implements MarketDataService{
	
	@Autowired
	FutureDictionary fDict;

	@Autowired
	RuntimeEngine rtEngine;
	
	@Autowired
	ContractRepo contractRepo;
	
	@Override
	public boolean subscribeContract(String gatewayId, String contractName) throws Exception {
		IAccount account = rtEngine.getAccount(gatewayId);
		ContractField contract = fDict.getContractByName(contractName);
		if(contract == null) {
			throw new NoSuchContractException(contractName);
		}
		boolean res = account.subscribe(contract);
		contractRepo.upsert(ContractInfo.convertFrom(contract));
		return res;
	}

	@Override
	public List<ContractInfo> getAllSubscribedContracts() throws Exception {
		return contractRepo.getAllSubscribedContracts();
	}

	@Override
	public List<ContractInfo> getAvailableContracts() {
		Collection<ContractField> contracts = fDict.getAvailableContracts();
		List<ContractInfo> resultList = new ArrayList<>(contracts.size());
		for(ContractField contract : contracts) {
			resultList.add(ContractInfo.convertFrom(contract));
		}
		return resultList;
	}

	
}
