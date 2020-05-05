package tech.xuanwu.northstar.entity;

import java.io.Serializable;

import lombok.Data;
import xyz.redtorch.pb.CoreEnum.ConnectStatusEnum;

@Data
public class AccountConnectionInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2635518323033389112L;

	private String accountId;
	
	private GatewayInfo gatewayInfo;
	
	public AccountConnectionInfo(String accountId, GatewayInfo gatewayInfo) {
		this.gatewayInfo = gatewayInfo;
		this.accountId = accountId;
	}
	
	public void onConnecting() {
		gatewayInfo.setStatus(ConnectStatusEnum.CS_Connecting);
	}
	
	public void onConnected() {
		gatewayInfo.setStatus(ConnectStatusEnum.CS_Connected);
	}
	
	public void onDisconnected() {
		gatewayInfo.setStatus(ConnectStatusEnum.CS_Disconnected);
	}
}
