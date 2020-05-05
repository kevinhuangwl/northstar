package tech.xuanwu.northstar.domain;

import java.util.List;

import tech.xuanwu.northstar.entity.AccountConnectionInfo;
import tech.xuanwu.northstar.entity.AccountInfo;
import tech.xuanwu.northstar.entity.GatewayInfo;
import tech.xuanwu.northstar.entity.OrderInfo;
import tech.xuanwu.northstar.entity.PositionInfo;
import tech.xuanwu.northstar.entity.TransactionInfo;
import tech.xuanwu.northstar.exception.TradeException;
import xyz.redtorch.pb.CoreField.CancelOrderReqField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;

/**
 * 账户接口
 * @author kevinhuangwl
 *
 */
public interface IAccount {
	
	String getAccountId();
	
	GatewayInfo getGatewayInfo();
	
	/**
	 * 获取账户信息
	 * @return
	 */
	AccountInfo getAccountInfo();
	
	/**
	 * 获取账户连接信息
	 * @return
	 */
	AccountConnectionInfo getAccountConnectionInfo();
	
	/**
	 * 更新账户
	 * @param account
	 */
	void updateAccount(AccountInfo account);
	
	/**
	 * 委托订单
	 * @param submitOrderReq
	 * @throws TradeException
	 */
	void submitOrder(SubmitOrderReqField submitOrderReq) throws TradeException;
	
	/**
	 * 撤销订单
	 * @param cancelOrderReq
	 * @throws TradeException
	 */
	void cancelOrder(CancelOrderReqField cancelOrderReq) throws TradeException;
	
	/**
	 * 更新持仓
	 * @param position
	 */
	void updatePosition(PositionInfo position);
	
	/**
	 * 更新订单
	 * @param order
	 */
	void updateOrder(OrderInfo order);

	/**
	 * 更新交易
	 * @param transaction
	 */
	void updateTransaction(TransactionInfo transaction);
	
	/**
	 * 获取持仓列表
	 * @return
	 */
	List<PositionInfo> getPositionInfoList();
	
	/**
	 * 获取当天订单列表
	 * @return
	 */
	List<OrderInfo> getOrderInfoList();
	
	/**
	 * 获取当天成交列表
	 * @return
	 */
	List<TransactionInfo> getTransactionInfoList();
	
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
	
	
	void onConnected();
}
