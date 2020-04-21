package tech.xuanwu.northstar.core.config.account;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import tech.xuanwu.northstar.core.config.factory.YamlPropertySourceFactory;
import tech.xuanwu.northstar.core.config.props.CtpGatewayCommonSettings;
import tech.xuanwu.northstar.domain.IAccount;

@Configuration
@PropertySource(value="classpath:account-simnow724.yml", factory=YamlPropertySourceFactory.class)
public class CtpSimnow724Config extends BaseAccountConfig{

	/****************************/
	/*         账户一			*/
	/****************************/
	@Bean("primarySetting")
	@ConfigurationProperties(prefix="account-primary")
	public CtpGatewayCommonSettings createSetting() {
		return new CtpGatewayCommonSettings();
	}
	@Bean("primaryAccount")
	public IAccount createAccount(@Qualifier("primarySetting") CtpGatewayCommonSettings setting) throws Exception {
		IAccount account = createCtpAccount(setting);
		rtEngine.regAccount(account);
		return account;
	}
	
	/****************************/
	/*         账户二			*/
	/****************************/
	@Bean("secondarySetting")
	@ConfigurationProperties(prefix="account-secondary")
	public CtpGatewayCommonSettings createSetting2() {
		return new CtpGatewayCommonSettings();
	}
	@Bean("secondaryAccount")
	public IAccount createAccount2(@Qualifier("secondarySetting") CtpGatewayCommonSettings setting) throws Exception {
		IAccount account = createCtpAccount(setting);
		rtEngine.regAccount(account);
		return account;
	}
	
}
