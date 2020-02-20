package tech.xuanwu.northstar.core.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.xuanwu.northstar.core.service.AccountService;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.exception.NoSuchAccountException;
import xyz.redtorch.pb.CoreField.AccountField;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.PositionField;
import xyz.redtorch.pb.CoreField.TradeField;

@Service
public class AccountServiceImpl implements AccountService{
	
	@Autowired
	RuntimeEngine rtEngine;

	@Override
	public List<AccountField> getAccountInfoList() {
		return rtEngine.getAccountInfoList();
	}

	@Override
	public List<PositionField> getPositionInfoList(String accountName) throws NoSuchAccountException {
		IAccount account = rtEngine.getAccount(accountName);
		return account.getPositionInfoList();
	}

	@Override
	public List<OrderField> getOrderInfoList(String accountName) throws NoSuchAccountException {
		IAccount account = rtEngine.getAccount(accountName);
		return account.getOrderInfoList(LocalDate.now(), LocalDate.now());
	}

	@Override
	public List<TradeField> getTransactionInfoList(String accountName) throws NoSuchAccountException {
		IAccount account = rtEngine.getAccount(accountName);
		return account.getTransactionInfoList(LocalDate.now(), LocalDate.now());
	}

	@Override
	public void connectGateway(String accountName) throws NoSuchAccountException {
		IAccount account = rtEngine.getAccount(accountName);
		account.connectGateway();
	}

	@Override
	public void disconnectGateway(String accountName) throws NoSuchAccountException {
		IAccount account = rtEngine.getAccount(accountName);
		account.disconnectGateway();
	}
	
}
