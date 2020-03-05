package tech.xuanwu.northstar.gateway;

import tech.xuanwu.northstar.engine.FastEventEngine;
import xyz.redtorch.pb.CoreField.CancelOrderReqField;
import xyz.redtorch.pb.CoreField.ContractField;
import xyz.redtorch.pb.CoreField.GatewayField;
import xyz.redtorch.pb.CoreField.GatewaySettingField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;
import xyz.redtorch.pb.CoreField.TickField;

public interface GatewayApi {

	/**
	 * 获取ID
	 * 
	 * @return
	 */
	String getGatewayId();

	/**
	 * 获取名称
	 * 
	 * @return
	 */
	String getGatewayName();

	/**
	 * 获取网关
	 * 
	 * @return
	 */
	GatewayField getGateway();
	
	/**
	 * 获取当前行情所属交易日
	 * @return
	 */
	String getTradingDay();

	/**
	 * 获取网关配置
	 * 
	 * @return
	 */
	GatewaySettingField getGatewaySetting();
	
	/**
	 * 订阅
	 * 
	 * @param subscribeReq
	 */
	boolean subscribe(ContractField contract);

	/**
	 * 退订
	 * 
	 * @param subscribeReq
	 */
	boolean unsubscribe(ContractField contract);

	/**
	 * 提交定单
	 * 
	 * @param orderReq
	 * @return
	 */
	String submitOrder(SubmitOrderReqField submitOrderReq);

	/**
	 * 撤销定单
	 * 
	 * @param cancelOrderReq
	 * @return
	 */
	boolean cancelOrder(CancelOrderReqField cancelOrderReq);
	
	/**
	 * 更新行情
	 * @param tick
	 */
	void emitTick(TickField tick);

	/**
	 * 获取事件引擎
	 * @return
	 */
	FastEventEngine getEventEngine();

	/**
	 * 连接
	 */
	void connect();

	/**
	 * 断开
	 */
	void disconnect();

	/**
	 * 网关连接状态
	 * 
	 * @return
	 */
	boolean isConnected();

	/**
	 * 获取登录错误标记
	 * 
	 * @return
	 */
	boolean getAuthErrorFlag();

	/**
	 * 设置登录错误标记
	 * 
	 * @return
	 */
	void setAuthErrorFlag(boolean loginErrorFlag);

	/**
	 * 获取最后一次开始登陆的时间戳
	 * 
	 * @return
	 */
	long getLastConnectBeginTimestamp();

}
