package tech.xuanwu.northstar.core.config.account;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.SimulatedGatewayImpl;
import tech.xuanwu.northstar.core.domain.Account;
import tech.xuanwu.northstar.core.persistence.repo.ContractRepo;
import tech.xuanwu.northstar.core.persistence.repo.CtpSettingRepo;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.FastEventEngine;
import tech.xuanwu.northstar.engine.IndexEngine;
import tech.xuanwu.northstar.entity.AccountInfo;
import tech.xuanwu.northstar.entity.ContractInfo;
import tech.xuanwu.northstar.entity.CtpSettingInfo;
import tech.xuanwu.northstar.entity.CtpSettingInfo.ConnectionType;
import tech.xuanwu.northstar.entity.CtpSettingInfo.MarketType;
import tech.xuanwu.northstar.entity.PositionInfo;
import tech.xuanwu.northstar.gateway.GatewayApi;
import xyz.redtorch.pb.CoreField.ContractField;
import xyz.redtorch.pb.CoreField.GatewaySettingField;

@Slf4j
@Configuration
public class CtpMarketDataConfig extends BaseAccountConfig implements InitializingBean{

	@Autowired
	private ContractRepo contractRepo;
	
	@Autowired
	private IndexEngine idxEngine;
	
	@Autowired
	private CtpSettingRepo ctpSettingRepo;
	
	@Autowired
	private DefaultListableBeanFactory beanFactory;
	
	/****************************/
	/*         行情网关			*/
	/****************************/
	@Override
	public void afterPropertiesSet() throws Exception {
		boolean isRealMarket = StringUtils.equals(profile, "prod");
		log.info("当前行情使用【{}行情】", isRealMarket ? "真实" : "仿真");
		List<CtpSettingInfo> mktGatewaySettingList = ctpSettingRepo.findByConnectionTypeAndMarketType(ConnectionType.MARKET, isRealMarket ? MarketType.REAL : MarketType.SIMULATE);
		for(CtpSettingInfo info : mktGatewaySettingList) {
			String gatewayName = info.getGatewayName();
			String gatewayId = info.getGatewayId();
			log.info("正在初始化【{}】", gatewayName);
			
			Class<?> gatewayClass = Class.forName(info.getGatewayClass());
			Constructor<?> constructor = gatewayClass.getConstructor(FastEventEngine.class, GatewaySettingField.class);
			GatewayApi gateway = (GatewayApi) constructor.newInstance(feEngine, info.convertTo());
			//行情网关默认自动连接
			gateway.connect();
			
			//注册入beanFactory
			beanFactory.registerSingleton(gatewayId, gateway);
			
			//默认为每个行情网关都注册一个模拟账户
			String accountId = info.getUserId() + "@" + info.getGatewayId();
			AccountInfo accountInfo = accountRepo.getLatestAccountInfoByAccountId(accountId);
			List<PositionInfo> positionInfoList = positionRepo.getPositionListByAccountId(accountId);
			GatewayApi simulatedGateway = new SimulatedGatewayImpl(gateway, feEngine, accountInfo, positionInfoList);
			
			IAccount account = new Account(simulatedGateway, accountRepo, positionRepo);
			rtEngine.regAccount(account);
			
			//自动续订合约
			CompletableFuture.runAsync(()->{
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
				}
				
				log.info("=====【{}】开始自动续订合约=====", gatewayName);
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
				
				account.connectGateway();
			}).exceptionally(e ->{
				log.error("", e);
				return null;
			});
		}
	}
}
