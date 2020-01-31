package tech.xuanwu.northstar.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tech.xuanwu.northstar.core.util.ContractMap;

@Configuration
public class CommonConfig {

	@Bean
	public ContractMap getContractHelper() {
		return new ContractMap();
	}
}
