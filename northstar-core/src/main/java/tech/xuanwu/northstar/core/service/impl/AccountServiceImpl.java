package tech.xuanwu.northstar.core.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.core.persistence.repo.GatewayRepo;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.entity.AccountInfo;
import tech.xuanwu.northstar.entity.GatewayInfo;
import tech.xuanwu.northstar.entity.OrderInfo;
import tech.xuanwu.northstar.entity.PositionInfo;
import tech.xuanwu.northstar.entity.TransactionInfo;
import tech.xuanwu.northstar.exception.NoSuchAccountException;
import tech.xuanwu.northstar.gateway.GatewayApi;
import tech.xuanwu.northstar.service.AccountService;
import xyz.redtorch.pb.CoreEnum.ConnectStatusEnum;
import xyz.redtorch.pb.CoreField.GatewayField;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {
	
	@Autowired
	RuntimeEngine rtEngine;
	
	@Autowired
	GatewayRepo gatewayRepo;
	
	@Override
	public List<AccountInfo> getAccountInfoList() {
		return rtEngine.getAccountInfoList();
	}

	@Override
	public List<PositionInfo> getPositionInfoList(String accountName) throws NoSuchAccountException {
		IAccount account = rtEngine.getAccount(accountName);
		return account.getPositionInfoList();
	}

	@Override
	public List<OrderInfo> getOrderInfoList(String accountName) throws NoSuchAccountException {
		IAccount account = rtEngine.getAccount(accountName);
		return account.getOrderInfoList(LocalDate.now(), LocalDate.now());
	}

	@Override
	public List<TransactionInfo> getTransactionInfoList(String accountName) throws NoSuchAccountException {
		IAccount account = rtEngine.getAccount(accountName);
		return account.getTransactionInfoList(LocalDate.now(), LocalDate.now());
	}

	@Override
	public void connectGateway(String accountName) throws Exception {
		log.info("建立账户【{}】的网关连接", accountName);
		IAccount account = rtEngine.getAccount(accountName);
		account.connectGateway();
		
		GatewayField gatewayField = account.getGateway();
		GatewayInfo gatewayInfo = GatewayInfo.convertFrom(gatewayField);
		gatewayInfo.setStatus(ConnectStatusEnum.CS_Connecting);
		gatewayRepo.upsertById(gatewayInfo);
		
		
	}

	@Override
	public void disconnectGateway(String accountName) throws NoSuchAccountException {
		log.info("断开账户【{}】的网关连接", accountName);
		IAccount account = rtEngine.getAccount(accountName);
		account.disconnectGateway();
		rtEngine.unregAccount(account.getName());
	}
	
}
