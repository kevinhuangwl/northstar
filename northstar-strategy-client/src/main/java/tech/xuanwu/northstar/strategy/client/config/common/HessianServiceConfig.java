package tech.xuanwu.northstar.strategy.client.config.common;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import tech.xuanwu.northstar.service.AccountService;
import tech.xuanwu.northstar.service.MailSenderService;
import tech.xuanwu.northstar.service.TradeService;
import tech.xuanwu.northstar.strategy.client.annotation.NorthstarService;

@Component
public class HessianServiceConfig {

	@NorthstarService
	AccountService accountService;
	
	@NorthstarService
	TradeService tradeService;
	
	@NorthstarService
	MailSenderService mailService;
	
	@Bean
	public AccountService getAccountService() {
		return accountService;
	}
	
	@Bean
	public TradeService getTradeService() {
		return tradeService;
	}
	
	@Bean
	public MailSenderService getMailSenderService() {
		return mailService;
	}
}
