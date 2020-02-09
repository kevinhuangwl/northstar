package tech.xuanwu.northstar.core.config;

import java.lang.reflect.Constructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.core.config.props.CtpGatewaySettingProperties;
import tech.xuanwu.northstar.core.domain.RealAccount;
import tech.xuanwu.northstar.core.domain.SimulateAccount;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.FastEventEngine;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.gateway.GatewayApi;
import xyz.redtorch.pb.CoreField.GatewaySettingField;

/**
 * gateway加载器
 * @author kevinhuangwl
 *
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(CtpGatewaySettingProperties.class)
public class GatewayApiLoader {

	@Autowired
	CtpGatewaySettingProperties p;
	
	@Autowired
	FastEventEngine feEngine;
	
	@Autowired
	RuntimeEngine rtEngine;
	
	@Bean(name="ctpGateway")
	public GatewayApi getCtpGatewayApi() throws Exception {
		Class<?> gatewayClass = Class.forName(p.getGatewayImplClassName());
		Constructor<?> c = gatewayClass.getConstructor(FastEventEngine.class, GatewaySettingField.class);
		GatewayApi gateway = (GatewayApi) c.newInstance(feEngine, p.convertToGatewaySettingField());
		gateway.connect();
		return gateway;
	}
	
	
	@Bean
	@ConditionalOnProperty(name="account.type", havingValue="real")
	public IAccount getAccount(@Autowired GatewayApi gatewayApi) {
		log.info("使用真实账户交易");
		IAccount account = new RealAccount(gatewayApi);
		rtEngine.regAccount(account);
		return account;
	}
	
	@Bean
	@ConditionalOnProperty(name="account.type", havingValue="simulate")
	public IAccount getSimulateAccount(@Autowired GatewayApi gatewayApi) {
		log.info("使用模拟账户交易");
		IAccount account = new SimulateAccount();
		rtEngine.regAccount(account);
		return account;
	}
}
