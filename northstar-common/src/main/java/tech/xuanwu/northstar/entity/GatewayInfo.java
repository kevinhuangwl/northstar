package tech.xuanwu.northstar.entity;

import java.io.Serializable;

import lombok.Data;
import xyz.redtorch.pb.CoreEnum.ConnectStatusEnum;
import xyz.redtorch.pb.CoreEnum.GatewayAdapterTypeEnum;
import xyz.redtorch.pb.CoreEnum.GatewayTypeEnum;
import xyz.redtorch.pb.CoreField.GatewayField;

@Data
public class GatewayInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6548138842343668803L;
	
	String gatewayId;  // 唯一标识
	String name;  // 名称
	String description;  // 描述
	GatewayTypeEnum gatewayType;  // 网关类型
	GatewayAdapterTypeEnum gatewayAdapterType;  // 网关适配器类型
	ConnectStatusEnum status = ConnectStatusEnum.CS_Disconnected;  // 网关状态
	boolean authErrorFlag; // 登录失败标志
	
	public static GatewayInfo convertFrom(GatewayField g) {
		GatewayInfo gi = new GatewayInfo();
		gi.gatewayId = g.getGatewayId();
		gi.name = g.getName();
		gi.description = g.getDescription();
		gi.gatewayType = g.getGatewayType();
		gi.gatewayAdapterType = g.getGatewayAdapterType();
		gi.status = g.getStatus();
		gi.authErrorFlag = g.getAuthErrorFlag();
		return gi;
	}
	
}
