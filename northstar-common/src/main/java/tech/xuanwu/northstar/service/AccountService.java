package tech.xuanwu.northstar.service;

import java.util.List;

import tech.xuanwu.northstar.entity.AccountInfo;
import tech.xuanwu.northstar.entity.OrderInfo;
import tech.xuanwu.northstar.entity.PositionInfo;
import tech.xuanwu.northstar.entity.TransactionInfo;
import tech.xuanwu.northstar.exception.NoSuchAccountException;

/**
 * 账户服务主要提供监控端所需要的账户相关的查询操作入口
 * @author kevinhuangwl
 *
 */
public interface AccountService {
	
	/**
	 * 获取账户信息
	 * @return
	 */
	List<AccountInfo> getAccountInfoList();
	
	/**
	 * 获取持仓信息
	 * @param accountGatewayId
	 * @return
	 */
	List<PositionInfo> getPositionInfoList(String accountGatewayId) throws NoSuchAccountException;
	
	/**
	 * 获取委托单信息
	 * @param accountGatewayId
	 * @return
	 */
	List<OrderInfo> getOrderInfoList(String accountGatewayId) throws NoSuchAccountException;
	
	/**
	 * 获取成交信息
	 * @param accountGatewayId
	 * @return
	 */
	List<TransactionInfo> getTransactionInfoList(String accountGatewayId) throws NoSuchAccountException;
	
	/**
	 * 连接网关
	 */
	void connectGateway() throws Exception ;
	
	/**
	 * 断开网关
	 * @param accountGatewayId
	 */
	void disconnectGateway(String accountGatewayId) throws NoSuchAccountException;
}
