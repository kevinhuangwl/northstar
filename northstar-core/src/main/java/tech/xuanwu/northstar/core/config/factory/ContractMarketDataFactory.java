package tech.xuanwu.northstar.core.config.factory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import tech.xuanwu.northstar.core.domain.ContractMarketData;
import tech.xuanwu.northstar.persistence.dao.BarDataDao;
import tech.xuanwu.northstar.persistence.dao.TickDataDao;

/**
 * 合约行情对象工厂
 * @author kevinhuangwl
 *
 */
@Configuration
public class ContractMarketDataFactory {
	
	@Bean
	@Scope("prototype")
	public ContractMarketData createContractMarketData(TickDataDao tickDao, BarDataDao barDao) {
		return new ContractMarketData(tickDao, barDao);
	}
	
}
