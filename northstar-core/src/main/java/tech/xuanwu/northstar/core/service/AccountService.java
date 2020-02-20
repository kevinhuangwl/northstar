package tech.xuanwu.northstar.core.service;

import java.util.List;

import tech.xuanwu.northstar.exception.NoSuchAccountException;
import xyz.redtorch.pb.CoreField.AccountField;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.PositionField;
import xyz.redtorch.pb.CoreField.TradeField;

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
	List<AccountField> getAccountInfoList();
	
	/**
	 * 获取持仓信息
	 * @param accountName
	 * @return
	 */
	List<PositionField> getPositionInfoList(String accountName) throws NoSuchAccountException;
	
	/**
	 * 获取委托单信息
	 * @param accountName
	 * @return
	 */
	List<OrderField> getOrderInfoList(String accountName) throws NoSuchAccountException;
	
	/**
	 * 获取成交信息
	 * @param accountName
	 * @return
	 */
	List<TradeField> getTransactionInfoList(String accountName) throws NoSuchAccountException;
	
	/**
	 * 连接网关
	 * @param accountName
	 */
	void connectGateway(String accountName) throws NoSuchAccountException;
	
	/**
	 * 断开网关
	 * @param accountName
	 */
	void disconnectGateway(String accountName) throws NoSuchAccountException;
}
