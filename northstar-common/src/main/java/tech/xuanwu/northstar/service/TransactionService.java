package tech.xuanwu.northstar.service;

import java.util.List;

import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.PositionField;

/**
 * 交易服务，用于提供账户交易操作
 * @author kevinhuangwl
 *
 */
public interface TransactionService {

	/**
	 * 下单
	 * @param gatewayId
	 * @param order
	 * @return
	 */
	boolean placeOrder(String gatewayId, OrderField order);
	
	/**
	 * 撤单
	 * @param gatewayId
	 * @param orderId
	 * @return
	 */
	boolean cancelOrder(String gatewayId, String orderId);
	
	/**
	 * 平仓所有持仓
	 * @param gatewayId
	 * @return
	 */
	boolean closeAllPosition(String gatewayId);
	
	/**
	 * 查询订单信息
	 * @param gatewayId
	 * @return
	 */
	List<OrderField> queryOrderStatus(String gatewayId);
	
	/**
	 * 查询持仓信息
	 * @param gatewayId
	 * @return
	 */
	List<PositionField> queryPosition(String gatewayId);
}
