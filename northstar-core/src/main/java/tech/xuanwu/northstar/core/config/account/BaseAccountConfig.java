package tech.xuanwu.northstar.core.config.account;

import java.lang.reflect.Constructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import tech.xuanwu.northstar.core.config.props.CtpGatewayCommonSettings;
import tech.xuanwu.northstar.core.domain.Account;
import tech.xuanwu.northstar.core.engine.gateway.event.TickEventLoopBackHandler;
import tech.xuanwu.northstar.core.persistence.repo.AccountRepo;
import tech.xuanwu.northstar.core.persistence.repo.PositionRepo;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.FastEventEngine;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.gateway.GatewayApi;
import xyz.redtorch.pb.CoreField.GatewaySettingField;

/***
 * 账户设置
 * 根据账户的实际数量来定义bean
 */
@Configuration
public class BaseAccountConfig {
	
	@Autowired(required=false)
	protected TickEventLoopBackHandler tickEventLoopBackHandler;
	
	@Autowired
	protected RuntimeEngine rtEngine;
	
	@Autowired
	protected AccountRepo accountRepo;
	
	@Autowired
	protected PositionRepo positionRepo;
	
	@Autowired
	protected FastEventEngine feEngine;
	
	protected IAccount createCtpAccount(CtpGatewayCommonSettings p) throws Exception {
		Class<?> gatewayClass = Class.forName(p.getGatewayImplClassName());
		Constructor<?> c = gatewayClass.getConstructor(FastEventEngine.class, GatewaySettingField.class);
		GatewayApi gateway = (GatewayApi) c.newInstance(feEngine, p.convertToGatewaySettingField());
		
//		//使用模拟账户时要初始化账户
//		if(!p.isRealTrader()) {
//			GatewayApi realGateway = gateway;
//			String gatewayId = realGateway.getGatewayId() + CommonConstant.SIM_TAG;
//			String gatewayName = realGateway.getGatewayName() + CommonConstant.SIM_TAG;
//			AccountInfo accountInfo = accountRepo.getLatestAccountInfoByName(gatewayName);
//			List<PositionInfo> positionInfoList = positionRepo.getPositionListByGateway(gatewayId);
//			gateway = new SimulatedGatewayImpl(realGateway, feEngine, accountInfo, positionInfoList);
//			
//			tickEventLoopBackHandler.setSimulatedGateway(gateway);
//		}
		
		IAccount account = new Account(gateway, accountRepo, positionRepo);
		return account;
	}
}
