package tech.xuanwu.northstar.domain;

import java.util.List;

import tech.xuanwu.northstar.entity.AccountInfo;
import xyz.redtorch.pb.CoreField.ContractField;
import xyz.redtorch.pb.CoreField.GatewayField;

/**
 * 账户接口
 * @author kevinhuangwl
 *
 */
public interface IAccount extends TradableAccount {
	
	String getGatewayId();
	
	String getName();
	
	GatewayField getGateway();
	
	/**
	 * 获取账户信息
	 * @return
	 */
	AccountInfo getAccountInfo();
	
	/**
	 * 更新账户
	 * @param account
	 */
	void updateAccount(AccountInfo account);
	
	/**
	 * 一键全平
	 */
	void sellOutAllPosition();
	
	/**
	 * 连线网关
	 */
	void connectGateway();
	
	/**
	 * 断开网关
	 */
	void disconnectGateway();
	
	/**
	 * 订阅合约
	 * @param contract
	 * @return
	 */
	boolean subscribe(ContractField contract);
	
}
