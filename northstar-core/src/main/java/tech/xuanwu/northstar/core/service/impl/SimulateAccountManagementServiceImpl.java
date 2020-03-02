package tech.xuanwu.northstar.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.core.service.SimulateAccountManagementService;
import tech.xuanwu.northstar.entity.AccountInfo;

@Slf4j
@Service
@ConditionalOnBean(type="MarketEngine")
public class SimulateAccountManagementServiceImpl implements SimulateAccountManagementService, InitializingBean{
	
	List<AccountInfo> simulateAccountList;

	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
	
	@Override
	public boolean createAccount(String accountName, String holderName, String gatewayId, double initialBalance,
			double commissionRate, double marginRate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCommissionRate(String accountName, double rate) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMarginRate(String accountName, double rate) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean dropAccount(String accountName) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double depositMoney(String accountName, double money) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double withdrawMoney(String accountName, double money) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<AccountInfo> getAllSimulateAccounts() {
		// TODO Auto-generated method stub
		return null;
	}


}
