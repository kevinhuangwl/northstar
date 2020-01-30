package tech.xuanwu.northstar.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tech.xuanwu.northstar.core.util.ContractHelper;

@Configuration
public class CommonConfig {

	@Bean
	public ContractHelper getContractHelper() {
		return new ContractHelper();
	}
}
