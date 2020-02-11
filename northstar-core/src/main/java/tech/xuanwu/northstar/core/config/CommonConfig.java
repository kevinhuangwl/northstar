package tech.xuanwu.northstar.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tech.xuanwu.northstar.core.util.FutureDictionary;

@Configuration
public class CommonConfig {

	@Bean
	public FutureDictionary getContractHelper() {
		return new FutureDictionary();
	}
}
