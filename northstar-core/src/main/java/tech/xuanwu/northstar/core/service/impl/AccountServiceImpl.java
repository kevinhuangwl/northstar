package tech.xuanwu.northstar.core.service.impl;

import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import tech.xuanwu.northstar.core.config.props.CtpGatewaySettingProperties;
import tech.xuanwu.northstar.core.domain.RealAccount;
import tech.xuanwu.northstar.core.domain.SimulateAccount;
import tech.xuanwu.northstar.core.persistence.repo.AccountRepo;
import tech.xuanwu.northstar.core.service.AccountService;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.FastEventEngine;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.entity.AccountInfo;
import tech.xuanwu.northstar.entity.OrderInfo;
import tech.xuanwu.northstar.entity.PositionInfo;
import tech.xuanwu.northstar.entity.TransactionInfo;
import tech.xuanwu.northstar.exception.NoSuchAccountException;
import tech.xuanwu.northstar.gateway.GatewayApi;
import xyz.redtorch.pb.CoreField.GatewaySettingField;

@Service
@EnableConfigurationProperties(CtpGatewaySettingProperties.class)
public class AccountServiceImpl implements AccountService, InitializingBean{
	
	@Autowired
	RuntimeEngine rtEngine;
	
	@Autowired
	CtpGatewaySettingProperties p;
	
	@Autowired
	FastEventEngine feEngine;
	
	@Autowired
	AccountRepo accountRepo;
	
	@Value("${account.useReal}")
	boolean useRealAccount;
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		//默认启动时自动连线
		connectGateway();
	}
	

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
		Class<?> gatewayClass = Class.forName(p.getGatewayImplClassName());
		Constructor<?> c = gatewayClass.getConstructor(FastEventEngine.class, GatewaySettingField.class);
		GatewayApi gateway = (GatewayApi) c.newInstance(feEngine, p.convertToGatewaySettingField());
		gateway.connect();
		
		IAccount account = useRealAccount ? new RealAccount(gateway, accountRepo) : new SimulateAccount(gateway, rtEngine, accountRepo);
		rtEngine.regAccount(account);
	}

	@Override
	public void disconnectGateway(String accountName) throws NoSuchAccountException {
		IAccount account = rtEngine.getAccount(accountName);
		account.disconnectGateway();
		rtEngine.unregAccount(account.getName());
	}
	
}
