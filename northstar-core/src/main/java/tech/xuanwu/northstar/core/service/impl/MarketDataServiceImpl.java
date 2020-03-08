package tech.xuanwu.northstar.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.xuanwu.northstar.core.persistence.repo.ContractRepo;
import tech.xuanwu.northstar.core.service.MarketDataService;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.entity.ContractInfo;
import tech.xuanwu.northstar.exception.NoSuchContractException;
import xyz.redtorch.pb.CoreField.ContractField;

@Service
public class MarketDataServiceImpl implements MarketDataService{
	

	@Autowired
	RuntimeEngine rtEngine;
	
	@Autowired
	ContractRepo contractRepo;
	
	@Override
	public boolean subscribeContract(String gatewayId, String symbol) throws Exception {
		IAccount account = rtEngine.getAccount(gatewayId);
		ContractInfo c = contractRepo.getContractBySymbol(gatewayId, symbol);
		if(c == null) {
			throw new NoSuchContractException(symbol);
		}
		ContractField contract = c.convertTo();
		boolean res = account.subscribe(contract);
		contractRepo.insertIfAbsent(ContractInfo.convertFrom(contract));
		return res;
	}

	@Override
	public List<ContractInfo> getAllSubscribedContracts(String gatewayId) throws Exception {
		return contractRepo.getAllSubscribedContracts(gatewayId);
	}

	@Override
	public List<ContractInfo> getAvailableContracts(String gatewayId) throws Exception {
		return contractRepo.getAllAvailableContracts(gatewayId);
	}

}
