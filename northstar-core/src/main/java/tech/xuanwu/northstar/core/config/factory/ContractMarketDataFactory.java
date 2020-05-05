package tech.xuanwu.northstar.core.config.factory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import tech.xuanwu.northstar.core.domain.ContractMarketData;

/**
 * 合约行情对象工厂
 * @author kevinhuangwl
 *
 */
@Configuration
public class ContractMarketDataFactory {
	
	@Bean
	@Scope("prototype")
	public ContractMarketData createContractMarketData() {
		return new ContractMarketData();
	}
	
}
