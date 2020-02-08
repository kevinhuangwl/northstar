package tech.xuanwu.northstar.core.config;

import java.lang.reflect.Constructor;
import java.util.EventObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tech.xuanwu.northstar.constant.EventType;
import tech.xuanwu.northstar.core.config.props.CtpGatewaySettingProperties;
import tech.xuanwu.northstar.core.domain.Account;
import tech.xuanwu.northstar.engine.FastEventEngine;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.gateway.GatewayApi;
import xyz.redtorch.pb.CoreField.GatewaySettingField;

/**
 * gateway加载器
 * @author kevinhuangwl
 *
 */
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
	public Account getAccount() {
		Account account = new Account(rtEngine);
		EventObject e = new EventObject(account);
		rtEngine.emitEvent(EventType.ACCOUNT_REGISTER.toString(), e);
		return account;
	}
	
}
