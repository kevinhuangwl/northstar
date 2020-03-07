package tech.xuanwu.northstar.core.service.impl;

import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.CtpGatewaySimulateImpl;
import tech.xuanwu.northstar.core.config.props.CtpGatewaySettings;
import tech.xuanwu.northstar.core.domain.Account;
import tech.xuanwu.northstar.core.persistence.repo.AccountRepo;
import tech.xuanwu.northstar.core.service.AccountService;
import tech.xuanwu.northstar.core.util.SimulateAccountFactory;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.FastEventEngine;
import tech.xuanwu.northstar.engine.MarketEngine;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.entity.AccountInfo;
import tech.xuanwu.northstar.entity.OrderInfo;
import tech.xuanwu.northstar.entity.PositionInfo;
import tech.xuanwu.northstar.entity.TransactionInfo;
import tech.xuanwu.northstar.exception.NoSuchAccountException;
import tech.xuanwu.northstar.gateway.GatewayApi;
import xyz.redtorch.pb.CoreField.GatewaySettingField;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {
	
	@Autowired
	RuntimeEngine rtEngine;
	
	@Autowired(required=false)
	MarketEngine mkEngine;
	
	@Autowired
	CtpGatewaySettings p;
	
	@Autowired
	FastEventEngine feEngine;
	
	@Autowired
	AccountRepo accountRepo;

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
	public void connectGateway() throws Exception {
		//FIXME 没有重复限制
		Class<?> gatewayClass = Class.forName(p.getGatewayImplClassName());
		Constructor<?> c = gatewayClass.getConstructor(FastEventEngine.class, GatewaySettingField.class);
		GatewayApi gateway = (GatewayApi) c.newInstance(feEngine, p.convertToGatewaySettingField());
		gateway.connect();
		
		//使用模拟账户时要初始化账户
		if(!p.isRealTrader()) {
			GatewayApi realGateway = gateway;
			
			AccountInfo account = accountRepo.getLatestAccountInfoByName(p.getGatewayName());
			if(account == null) {
				account = SimulateAccountFactory.createAccount(p.getGatewayName());
			}
			
			gateway = new CtpGatewaySimulateImpl(realGateway, feEngine, account);
		}
		
		log.info("连接网关【{}】，使用【{}】交易", p.getGatewayID(), p.isRealTrader()?"真实账户":"模拟账户");
		IAccount account = new Account(gateway, accountRepo);
		rtEngine.regAccount(account);
	}

	@Override
	public void disconnectGateway(String accountName) throws NoSuchAccountException {
		log.info("断开账户【{}】的网关连接", accountName);
		IAccount account = rtEngine.getAccount(accountName);
		account.disconnectGateway();
		rtEngine.unregAccount(account.getName());
	}
	
}
