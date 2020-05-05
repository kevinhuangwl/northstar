package tech.xuanwu.northstar.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.core.persistence.repo.GatewayRepo;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.entity.AccountConnectionInfo;
import tech.xuanwu.northstar.entity.AccountInfo;
import tech.xuanwu.northstar.entity.OrderInfo;
import tech.xuanwu.northstar.entity.PositionInfo;
import tech.xuanwu.northstar.entity.TransactionInfo;
import tech.xuanwu.northstar.exception.NoSuchAccountException;
import tech.xuanwu.northstar.service.AccountService;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {
	
	@Autowired
	RuntimeEngine rtEngine;
	
	@Autowired
	GatewayRepo gatewayRepo;
	
	@Override
	public List<AccountConnectionInfo> getAccountList() {
		List<IAccount> accountList = rtEngine.getAccountList();
		List<AccountConnectionInfo> resultList = new ArrayList<>(accountList.size());
		for(IAccount account : accountList) {
			resultList.add(account.getAccountConnectionInfo());
		}
		return resultList;
	}

	@Override
	public AccountInfo getAccountInfo(String accountId) throws NoSuchAccountException {
		return rtEngine.getAccount(accountId).getAccountInfo();
	}
	

	@Override
	public List<PositionInfo> getPositionInfoList(String accountId) throws NoSuchAccountException {
		IAccount account = rtEngine.getAccount(accountId);
		return account.getPositionInfoList();
	}

	@Override
	public List<OrderInfo> getOrderInfoList(String accountId) throws NoSuchAccountException {
		IAccount account = rtEngine.getAccount(accountId);
		return account.getOrderInfoList();
	}

	@Override
	public List<TransactionInfo> getTransactionInfoList(String accountId) throws NoSuchAccountException {
		IAccount account = rtEngine.getAccount(accountId);
		return account.getTransactionInfoList();
	}

	@Override
	public void connect(String accountId) throws Exception {
		log.info("建立账户【{}】的网关连接", accountId);
		IAccount account = rtEngine.getAccount(accountId);
		account.connectGateway();
	}

	@Override
	public void disconnect(String accountId) throws NoSuchAccountException {
		log.info("断开账户【{}】的网关连接", accountId);
		IAccount account = rtEngine.getAccount(accountId);
		account.disconnectGateway();
	}

	

}
