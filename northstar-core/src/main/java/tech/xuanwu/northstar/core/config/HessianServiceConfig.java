package tech.xuanwu.northstar.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.caucho.HessianServiceExporter;

import tech.xuanwu.northstar.service.AccountService;
import tech.xuanwu.northstar.service.MailSenderService;

@Configuration
public class HessianServiceConfig {

	@Bean(name = "/MailSenderService")
    public HessianServiceExporter mailService(MailSenderService mailService) {
		return commonRegistry(mailService, MailSenderService.class);
    }
	
	@Bean(name = "/AccountService")
    public HessianServiceExporter accountService(AccountService accountService) {
        return commonRegistry(accountService, AccountService.class);
    }
	
	private HessianServiceExporter commonRegistry(Object obj, Class<?> interfaceClass) {
		HessianServiceExporter exporter = new HessianServiceExporter();
        exporter.setService(obj);
        exporter.setServiceInterface(interfaceClass);
        return exporter;
	}
}
