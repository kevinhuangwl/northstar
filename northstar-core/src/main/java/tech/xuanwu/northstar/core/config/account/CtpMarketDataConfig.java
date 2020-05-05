package tech.xuanwu.northstar.core.config.account;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.SimulatedGatewayImpl;
import tech.xuanwu.northstar.constant.CommonConstant;
import tech.xuanwu.northstar.core.config.factory.YamlPropertySourceFactory;
import tech.xuanwu.northstar.core.config.props.CtpGatewayCommonSettings;
import tech.xuanwu.northstar.core.domain.Account;
import tech.xuanwu.northstar.core.persistence.repo.ContractRepo;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.FastEventEngine;
import tech.xuanwu.northstar.engine.IndexEngine;
import tech.xuanwu.northstar.entity.AccountInfo;
import tech.xuanwu.northstar.entity.ContractInfo;
import tech.xuanwu.northstar.entity.PositionInfo;
import tech.xuanwu.northstar.gateway.GatewayApi;
import xyz.redtorch.pb.CoreEnum.GatewayTypeEnum;
import xyz.redtorch.pb.CoreField.ContractField;
import xyz.redtorch.pb.CoreField.GatewaySettingField;

@Slf4j
@Configuration
@PropertySource(value="classpath:account-ctp.yml", factory=YamlPropertySourceFactory.class)
public class CtpMarketDataConfig extends BaseAccountConfig{

	@Autowired
	private ContractRepo contractRepo;
	
	@Autowired
	private ApplicationContext ctx;
	
	@Autowired
	private IndexEngine idxEngine;
	
	@Resource(name = "ctpMdAccountSetting")
	CtpGatewayCommonSettings setting;
	
	/****************************/
	/*         行情网关			*/
	/****************************/
	@Bean("ctpMdAccountSetting")
	public CtpGatewayCommonSettings ctpMdAccountSetting() {
		return new CtpGatewayCommonSettings();
	}
	
	//独立数据源
	@Bean(CommonConstant.CTP_MKT_GATEWAY)
	public GatewayApi createCtpMdAccount() throws Exception {
		setting.setGatewayType(GatewayTypeEnum.GTE_MarketData);
		Class<?> gatewayClass = Class.forName(setting.getGatewayImplClassName());
		Constructor<?> constructor = gatewayClass.getConstructor(FastEventEngine.class, GatewaySettingField.class);
		GatewayApi gateway = (GatewayApi) constructor.newInstance(feEngine, setting.convertToGatewaySettingField());
		gateway.connect();
		
		CompletableFuture.runAsync(()->{
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
			}
			
			log.info("=====开始自动续订合约=====");
			//自动续订阅合约
			List<ContractInfo> contractList;
			try {
				contractList = contractRepo.getAllSubscribedContracts(gateway.getGatewayId());
			} catch (Exception ex) {
				log.error("", ex);
				throw new RuntimeException(ex);
			}
			for(ContractInfo c : contractList) {
				ContractField contract = c.convertTo();
				if(contract != null) {
					gateway.subscribe(contract);
					log.info("订阅网关【{}】的合约【{}】", c.getGatewayId(), c.getSymbol());
				}else {
					log.warn("合约【{}】已过期", c.getSymbol());
					contractRepo.delete(c.getGatewayId(),c.getSymbol());				
				}
			}		
			
			//自动续订指数合约
			try {
				idxEngine.onGatewayReady(gateway.getGatewayId());
			} catch (Exception ex) {
				log.error("", ex);
				throw new RuntimeException(ex);
			}
			
			log.info("=====自动续订合约完成=====");
		}).exceptionally(e ->{
			log.error("", e);
			return null;
		});
		
		
		return gateway;
	}
	
	
	@Bean("simulatedGateway")
	@ConditionalOnExpression("${account.simulate}")
	public GatewayApi createSimulatedGateway() throws Exception {
		//使用模拟账户时要初始化账户
		GatewayApi realGateway = (GatewayApi) ctx.getBean(CommonConstant.CTP_MKT_GATEWAY);
		String accountId = setting.getUserID();
		AccountInfo accountInfo = accountRepo.getLatestAccountInfoByAccountId(accountId);
		List<PositionInfo> positionInfoList = positionRepo.getPositionListByAccountId(accountId);
		GatewayApi simulatedGateway = new SimulatedGatewayImpl(realGateway, feEngine, accountInfo, positionInfoList);
		
		IAccount account = new Account(simulatedGateway, accountRepo, positionRepo);
		rtEngine.regAccount(account);
		return simulatedGateway;
	}
}
