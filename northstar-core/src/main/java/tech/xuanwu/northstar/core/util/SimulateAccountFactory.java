package tech.xuanwu.northstar.core.util;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.entity.AccountInfo;
import xyz.redtorch.pb.CoreEnum.CurrencyEnum;

@Slf4j
public class SimulateAccountFactory {
	
	/*默认模拟账户金额10万*/
	private static final int DEFAULT_BALANCE = 100000;
	
	private static final String SIM_TAG = "@Simulate";

	private SimulateAccountFactory() {}
	
	public static AccountInfo createAccount(String gatewayName) {
		log.info("创建模拟账户{}", gatewayName);
		AccountInfo info = new AccountInfo();
		info.setAccountId(gatewayName + SIM_TAG);
		info.setName(gatewayName + SIM_TAG);
		info.setAvailable(DEFAULT_BALANCE);
		info.setBalance(DEFAULT_BALANCE);
		info.setCode(gatewayName);
		info.setCurrency(CurrencyEnum.CNY);
		info.setGatewayId(gatewayName + SIM_TAG);
		info.setHolder("user");
		return info;
	}
}
