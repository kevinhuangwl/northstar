package tech.xuanwu.northstar.core.config.account;

import java.lang.reflect.Constructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import tech.xuanwu.northstar.core.domain.Account;
import tech.xuanwu.northstar.core.persistence.repo.AccountRepo;
import tech.xuanwu.northstar.core.persistence.repo.ContractRepo;
import tech.xuanwu.northstar.core.persistence.repo.PositionRepo;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.FastEventEngine;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.entity.CtpSettingInfo;
import tech.xuanwu.northstar.gateway.GatewayApi;
import xyz.redtorch.pb.CoreField.GatewaySettingField;

/***
 * 账户设置
 * 根据账户的实际数量来定义bean
 */
@Configuration
public class BaseAccountConfig {
	
	@Autowired
	protected RuntimeEngine rtEngine;
	
	@Autowired
	protected AccountRepo accountRepo;
	
	@Autowired
	protected PositionRepo positionRepo;
	
	@Autowired
	protected ContractRepo contractRepo;
	
	@Autowired
	protected FastEventEngine feEngine;
	
	@Value("${spring.profiles.active:prod}")
	protected String profile;
	
	protected IAccount createCtpAccount(CtpSettingInfo setting) throws Exception {
		Class<?> gatewayClass = Class.forName(setting.getGatewayClass());
		Constructor<?> c = gatewayClass.getConstructor(FastEventEngine.class, GatewaySettingField.class);
		GatewayApi gateway = (GatewayApi) c.newInstance(feEngine, setting.convertTo());
		IAccount account = new Account(gateway, accountRepo, positionRepo, contractRepo);
		return account;
	}
}
