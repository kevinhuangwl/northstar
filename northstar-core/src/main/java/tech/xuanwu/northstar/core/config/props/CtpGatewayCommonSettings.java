package tech.xuanwu.northstar.core.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Optional;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import xyz.redtorch.pb.CoreEnum.GatewayAdapterTypeEnum;
import xyz.redtorch.pb.CoreEnum.GatewayTypeEnum;
import xyz.redtorch.pb.CoreField.GatewaySettingField;
import xyz.redtorch.pb.CoreField.GatewaySettingField.CtpApiSettingField;

/**
 * 	CTP网关配置加载类
 * @author kevinhuangwl
 *
 */

@Slf4j
@Getter
@Setter
@ConfigurationProperties(prefix="account")
public class CtpGatewayCommonSettings {
	//网关类型
	private GatewayTypeEnum gatewayType = GatewayTypeEnum.GTE_Trade;
	//网关适配器类型
	private GatewayAdapterTypeEnum adpterType = GatewayAdapterTypeEnum.GAT_CTP;
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
		
		ctpApiBuilder.setAppId(Optional.fromNullable(appID).or(""));
		ctpApiBuilder.setAuthCode(Optional.fromNullable(authCode).or(""));
		ctpApiBuilder.setBrokerId(Optional.fromNullable(brokerID).or(""));
		ctpApiBuilder.setMdHost(mdHost);
		ctpApiBuilder.setMdPort(mdPort);
		ctpApiBuilder.setTdHost(tdHost);
		ctpApiBuilder.setTdPort(tdPort);
		ctpApiBuilder.setUserId(userID);
		ctpApiBuilder.setPassword(password);
		ctpApiBuilder.setUserProductInfo(Optional.fromNullable(userProductInfo).or(""));
		
		builder.setGatewayAdapterType(adpterType);
		builder.setGatewayId(gatewayID);
		builder.setGatewayName(Optional.fromNullable(gatewayName).or(""));
		builder.setGatewayType(gatewayType);
		builder.setCtpApiSetting(ctpApiBuilder);
		
		log.info("当前配置信息：\n" + JSON.toJSONString(this, true));
		
		return builder.build();
	}
}
