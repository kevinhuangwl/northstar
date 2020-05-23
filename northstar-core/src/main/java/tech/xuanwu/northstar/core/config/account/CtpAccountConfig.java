package tech.xuanwu.northstar.core.config.account;

import java.util.List;

import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.core.persistence.repo.CtpSettingRepo;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.entity.CtpSettingInfo;
import tech.xuanwu.northstar.entity.CtpSettingInfo.ConnectionType;
import tech.xuanwu.northstar.entity.CtpSettingInfo.MarketType;

@Slf4j
@Configuration
public class CtpAccountConfig extends BaseAccountConfig implements InitializingBean{

	@Autowired
	private CtpSettingRepo ctpSettingRepo;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		List<CtpSettingInfo> accountSettingList = ctpSettingRepo.findByConnectionTypeAndMarketType(ConnectionType.ACCOUNT, StringUtils.equals(profile, "prod") ? MarketType.REAL : MarketType.SIMULATE);
		for(CtpSettingInfo info : accountSettingList) {
			String gatewayName = info.getGatewayName();
			log.info("正在初始化【{}】", gatewayName);
			
			IAccount account = createCtpAccount(info);
			rtEngine.regAccount(account);
		}
	}

}
