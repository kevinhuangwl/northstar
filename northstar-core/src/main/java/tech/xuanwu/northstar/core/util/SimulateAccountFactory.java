package tech.xuanwu.northstar.core.util;

import tech.xuanwu.northstar.entity.AccountInfo;

public class SimulateAccountFactory {
	
	private static final int DEFAULT_BALANCE = 100000;

	private SimulateAccountFactory() {}
	
	public static AccountInfo createAccount(String gatewayName) {
		AccountInfo info = new AccountInfo();
		info.setAccountId(gatewayName);
		info.setName(gatewayName);
		info.setAvailable(DEFAULT_BALANCE);
		info.setBalance(DEFAULT_BALANCE);
		
		return info;
	}
}
