package tech.xuanwu.northstar.core.service.impl;

import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.SimulatedGatewayImpl;
import tech.xuanwu.northstar.constant.CommonConstant;
import tech.xuanwu.northstar.core.config.props.CtpGatewaySettings;
import tech.xuanwu.northstar.core.domain.Account;
import tech.xuanwu.northstar.core.engine.gateway.event.TickEventLoopBackHandler;
import tech.xuanwu.northstar.core.persistence.repo.AccountRepo;
import tech.xuanwu.northstar.core.persistence.repo.GatewayRepo;
import tech.xuanwu.northstar.core.persistence.repo.PositionRepo;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.FastEventEngine;
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
import xyz.redtorch.pb.CoreField.GatewaySettingField;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {
	
	@Autowired
	RuntimeEngine rtEngine;
	
	@Autowired(required=false)
	TickEventLoopBackHandler tickEventLoopBackHandler;
	
	@Autowired
	CtpGatewaySettings p;
	
	@Autowired
	FastEventEngine feEngine;
	
	@Autowired
	AccountRepo accountRepo;
	
	@Autowired
	PositionRepo positionRepo;
	
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
	public void connectGateway() throws Exception {
		//FIXME 没有重复限制
		Class<?> gatewayClass = Class.forName(p.getGatewayImplClassName());
		Constructor<?> c = gatewayClass.getConstructor(FastEventEngine.class, GatewaySettingField.class);
		GatewayApi gateway = (GatewayApi) c.newInstance(feEngine, p.convertToGatewaySettingField());
		
		//使用模拟账户时要初始化账户
		if(!p.isRealTrader()) {
			GatewayApi realGateway = gateway;
			String gatewayId = realGateway.getGatewayId() + CommonConstant.SIM_TAG;
			String gatewayName = realGateway.getGatewayName() + CommonConstant.SIM_TAG;
			AccountInfo accountInfo = accountRepo.getLatestAccountInfoByName(gatewayName);
			List<PositionInfo> positionInfoList = positionRepo.getPositionListByGateway(gatewayId);
			gateway = new SimulatedGatewayImpl(realGateway, feEngine, accountInfo, positionInfoList);
			
			tickEventLoopBackHandler.setSimulatedGateway(gateway);
		}
		
		GatewayInfo gatewayInfo = new GatewayInfo();
		gatewayInfo.setGatewayId(gateway.getGatewayId());
		gatewayInfo.setName(gateway.getGatewayName());
		gatewayInfo.setGatewayAdapterType(gateway.getGatewaySetting().getGatewayAdapterType());
		gatewayInfo.setGatewayType(gateway.getGateway().getGatewayType());
		gatewayInfo.setStatus(ConnectStatusEnum.CS_Connecting);
		gatewayRepo.upsertById(gatewayInfo);
		
		log.info("连接网关【{}】，使用【{}】交易", p.getGatewayName(), p.isRealTrader()?"真实账户":"模拟账户");
		IAccount account = new Account(gateway, accountRepo, positionRepo);
		rtEngine.regAccount(account);
		
		gateway.connect();
	}

	@Override
	public void disconnectGateway(String accountName) throws NoSuchAccountException {
		log.info("断开账户【{}】的网关连接", accountName);
		IAccount account = rtEngine.getAccount(accountName);
		account.disconnectGateway();
		rtEngine.unregAccount(account.getName());
	}
	
}
