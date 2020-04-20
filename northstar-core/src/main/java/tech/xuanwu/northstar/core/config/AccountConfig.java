package tech.xuanwu.northstar.core.config;

import java.lang.reflect.Constructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import tech.xuanwu.northstar.constant.CommonConstant;
import tech.xuanwu.northstar.core.config.factory.YamlPropertySourceFactory;
import tech.xuanwu.northstar.core.config.props.CtpGatewaySettings;
import tech.xuanwu.northstar.core.domain.Account;
import tech.xuanwu.northstar.core.engine.gateway.event.TickEventLoopBackHandler;
import tech.xuanwu.northstar.core.persistence.repo.AccountRepo;
import tech.xuanwu.northstar.core.persistence.repo.PositionRepo;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.FastEventEngine;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.gateway.GatewayApi;
import xyz.redtorch.pb.CoreEnum.GatewayTypeEnum;
import xyz.redtorch.pb.CoreField.GatewaySettingField;

/***
 * 账户设置
 * 根据账户的实际数量来定义bean
 */
@Configuration
@PropertySource(value="classpath:account-setting.yml", factory=YamlPropertySourceFactory.class)
public class AccountConfig {
	
	@Autowired(required=false)
	TickEventLoopBackHandler tickEventLoopBackHandler;
	
	@Autowired
	RuntimeEngine rtEngine;
	
	@Autowired
	AccountRepo accountRepo;
	
	@Autowired
	PositionRepo positionRepo;
	
	@Autowired
	FastEventEngine feEngine;
	
	/****************************/
	/*         账户一			*/
	/****************************/
	@Bean("simnow724_094020Setting")
	@ConfigurationProperties(prefix="account.simnow724-094020")
	public CtpGatewaySettings createSimnow724_094020Setting() {
		return new CtpGatewaySettings();
	}
	@Bean("simnow724_094020")
	public IAccount createSimnow724_094020(@Qualifier("simnow724_094020Setting") CtpGatewaySettings setting) throws Exception {
		IAccount account = createCtpAccount(setting);
		rtEngine.regAccount(account);
		return account;
	}
	
	/****************************/
	/*         账户二			*/
	/****************************/
	@Bean("simnow724_163457Setting")
	@ConfigurationProperties(prefix="account.simnow724-163457")
	public CtpGatewaySettings createSimnow724_163457Setting() {
		return new CtpGatewaySettings();
	}
	@Bean("simnow724_163457")
	public IAccount createSimnow724_163457(@Qualifier("simnow724_163457Setting") CtpGatewaySettings setting) throws Exception {
		IAccount account = createCtpAccount(setting);
		rtEngine.regAccount(account);
		return account;
	}
	
	
	/****************************/
	/*         行情网关			*/
	/****************************/
	@Bean("ctpMdAccountSetting")
	@ConfigurationProperties(prefix="account.ctp-simnow")
	public CtpGatewaySettings ctpMdAccountSetting() {
		return new CtpGatewaySettings();
	}
	//独立数据源
	@Bean(CommonConstant.CTP_MKT_GATEWAY)
	public GatewayApi createCtpMdAccount(@Qualifier("ctpMdAccountSetting") CtpGatewaySettings setting) throws Exception {
		setting.setGatewayType(GatewayTypeEnum.GTE_MarketData);
		Class<?> gatewayClass = Class.forName(setting.getGatewayImplClassName());
		Constructor<?> c = gatewayClass.getConstructor(FastEventEngine.class, GatewaySettingField.class);
		GatewayApi gateway = (GatewayApi) c.newInstance(feEngine, setting.convertToGatewaySettingField());
		gateway.connect();
		return gateway;
	}
	
	
	
	private IAccount createCtpAccount(CtpGatewaySettings p) throws Exception {
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
