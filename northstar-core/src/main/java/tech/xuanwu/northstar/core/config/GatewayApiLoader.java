package tech.xuanwu.northstar.core.config;

import java.lang.reflect.Constructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tech.xuanwu.northstar.core.config.props.CtpGatewaySettingProperties;
import tech.xuanwu.northstar.gateway.GatewayApi;
import tech.xuanwu.northstar.service.FastEventService;
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
	FastEventService fastEventService;
	
	@Bean(name="ctpGateway")
	public GatewayApi getCtpGatewayApi() throws Exception {
		Class<?> gatewayClass = Class.forName(p.getGatewayImplClassName());
		Constructor<?> c = gatewayClass.getConstructor(FastEventService.class, GatewaySettingField.class);
		GatewayApi gateway = (GatewayApi) c.newInstance(fastEventService, p.convertToGatewaySettingField());
		gateway.connect();
		return gateway;
	}
	
}
