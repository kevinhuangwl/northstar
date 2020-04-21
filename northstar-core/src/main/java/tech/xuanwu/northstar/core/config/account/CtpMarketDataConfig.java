package tech.xuanwu.northstar.core.config.account;

import java.lang.reflect.Constructor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import tech.xuanwu.northstar.constant.CommonConstant;
import tech.xuanwu.northstar.core.config.factory.YamlPropertySourceFactory;
import tech.xuanwu.northstar.core.config.props.CtpGatewayCommonSettings;
import tech.xuanwu.northstar.engine.FastEventEngine;
import tech.xuanwu.northstar.gateway.GatewayApi;
import xyz.redtorch.pb.CoreEnum.GatewayTypeEnum;
import xyz.redtorch.pb.CoreField.GatewaySettingField;

@Configuration
@PropertySource(value="classpath:account-ctp.yml", factory=YamlPropertySourceFactory.class)
public class CtpMarketDataConfig extends BaseAccountConfig{

	
	/****************************/
	/*         行情网关			*/
	/****************************/
	@Bean("ctpMdAccountSetting")
	public CtpGatewayCommonSettings ctpMdAccountSetting() {
		return new CtpGatewayCommonSettings();
	}
	//独立数据源
	@Bean(CommonConstant.CTP_MKT_GATEWAY)
	public GatewayApi createCtpMdAccount(@Qualifier("ctpMdAccountSetting") CtpGatewayCommonSettings setting) throws Exception {
		setting.setGatewayType(GatewayTypeEnum.GTE_MarketData);
		Class<?> gatewayClass = Class.forName(setting.getGatewayImplClassName());
		Constructor<?> c = gatewayClass.getConstructor(FastEventEngine.class, GatewaySettingField.class);
		GatewayApi gateway = (GatewayApi) c.newInstance(feEngine, setting.convertToGatewaySettingField());
		gateway.connect();
		return gateway;
	}
}
