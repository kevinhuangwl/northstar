package tech.xuanwu.northstar.core.trade;

import xyz.redtorch.pb.CoreField.AccountField;
import xyz.redtorch.pb.CoreField.OrderField;

/**
 * 风控接口
 * @author kevinhuangwl
 *
 */
public interface AccountOperation {

	/**
	 * 下单
	 * @return
	 */
	boolean placeOrder();
	
	/**
	 * 撤单
	 * @return
	 */
	boolean cancelOrder();
	
	/**
	 * 获取账户信息
	 * @return
	 */
	AccountField getAccountInfo();
	
	/**
	 * 获取订单信息
	 * @return
	 */
	OrderField getOrderInfo();
}
