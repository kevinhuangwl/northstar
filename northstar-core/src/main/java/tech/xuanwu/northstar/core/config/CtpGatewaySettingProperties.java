package tech.xuanwu.northstar.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;
import xyz.redtorch.pb.CoreEnum.GatewayAdapterTypeEnum;
import xyz.redtorch.pb.CoreEnum.GatewayTypeEnum;
import xyz.redtorch.pb.CoreField.GatewaySettingField;
import xyz.redtorch.pb.CoreField.GatewaySettingField.CtpApiSettingField;

/**
 * 	CTP网关配置加载类
 * @author kevinhuangwl
 *
 */

@Getter
@Setter
@ConfigurationProperties(prefix="ctp")
public class CtpGatewaySettingProperties {
	//网关类型
	private GatewayTypeEnum gatewayType = GatewayTypeEnum.TRADE_AND_MARKET_DATA;
	//网关适配器类型
	private GatewayAdapterTypeEnum adpterType = GatewayAdapterTypeEnum.CTP;
	//网关实现类名称
	private String gatewayImplClassName;
	//网关ID
	private String gatewayID;
	//网关名称
	private String gatewayName;
	//行情主机IP
	private String mdHost;
	//行情主机端口
	private String mdPort;
	//交易主机IP
	private String tdHost;
	//交易主机端口
	private String tdPort;
	//用户名
	private String userID;
	//密码
	private String password;
	
	private String appID;
	
	private String userProductInfo;
	//授权码
	private String authCode;
	//期货公司ID
	private String brokerID;
	

	public GatewaySettingField convertToGatewaySettingField() {
		GatewaySettingField.Builder builder = GatewaySettingField.newBuilder();
		CtpApiSettingField.Builder ctpApiBuilder = CtpApiSettingField.newBuilder();
		
		ctpApiBuilder.setAppId(appID);
		ctpApiBuilder.setAuthCode(authCode);
		ctpApiBuilder.setBrokerId(brokerID);
		ctpApiBuilder.setMdHost(mdHost);
		ctpApiBuilder.setMdPort(mdPort);
		ctpApiBuilder.setTdHost(tdHost);
		ctpApiBuilder.setTdPort(tdPort);
		ctpApiBuilder.setUserId(userID);
		ctpApiBuilder.setPassword(password);
		ctpApiBuilder.setUserProductInfo(userProductInfo);
		
		builder.setGatewayAdapterType(adpterType);
		builder.setGatewayId(gatewayID);
		builder.setGatewayName(gatewayName);
		builder.setGatewayType(gatewayType);
		builder.setCtpApiSetting(ctpApiBuilder);
		
		return builder.build();
	}
}
