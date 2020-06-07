package tech.xuanwu.northstar.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import xyz.redtorch.pb.CoreEnum.GatewayTypeEnum;
import xyz.redtorch.pb.CoreField.GatewaySettingField;
import xyz.redtorch.pb.CoreField.GatewaySettingField.CtpApiSettingField;

@Data
@Document
public class CtpSettingInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8735223670483026841L;
	@Id
	private String id;
	private String gatewayClass = "xyz.redtorch.gateway.ctp.x64v6v3v15v.CtpGatewayImpl";
	private String gatewayId;
	private String gatewayName;
	private String userId;
	private String password;
	private String brokerId;
	private String tdHost;
	private String tdPort;
	private String mdHost;
	private String mdPort;
	private String authCode;
	private String userProductInfo; 
	private String appId;
	private ConnectionType connectionType;
	private MarketType marketType;
	
	public static enum ConnectionType {
		//行情网关
		MARKET,
		//账户网关
		ACCOUNT
	}
	
	public static enum MarketType {
		//仿真
		SIMULATE,
		//测试
		TEST,
		//真实
		REAL
	}
	
	
	public GatewaySettingField convertTo() {
		GatewaySettingField.Builder builder = GatewaySettingField.newBuilder()
				.setGatewayId(gatewayId)
				.setGatewayName(gatewayName)
				.setGatewayType(ConnectionType.MARKET == connectionType ? GatewayTypeEnum.GTE_MarketData : GatewayTypeEnum.GTE_Trade)
				.setImplementClassName(gatewayClass)
				.setCtpApiSetting(CtpApiSettingField.newBuilder()
						.setAppId(appId)
						.setAuthCode(authCode)
						.setBrokerId(brokerId)
						.setMdHost(mdHost)
						.setMdPort(mdPort)
						.setTdHost(tdHost)
						.setTdPort(tdPort)
						.setUserId(userId)
						.setPassword(password)
						.setUserProductInfo(userProductInfo)
						.build());
		return builder.build();
	}
}
