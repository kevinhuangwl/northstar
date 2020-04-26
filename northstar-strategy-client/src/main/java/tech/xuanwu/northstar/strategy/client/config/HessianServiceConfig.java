package tech.xuanwu.northstar.strategy.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import tech.xuanwu.northstar.service.AccountService;
import tech.xuanwu.northstar.service.TradeService;
import tech.xuanwu.northstar.strategy.client.annotation.NorthstarService;

@Component
public class HessianServiceConfig {

	@NorthstarService
	AccountService accountService;
	
	@NorthstarService
	TradeService tradeService;
	
	@Bean
	public AccountService getAccountService() {
		return accountService;
	}
	
	@Bean
	public TradeService getTradeService() {
		return tradeService;
	}
}
