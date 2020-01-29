package tech.xuanwu.northstar.core.trade;

import xyz.redtorch.pb.CoreField.AccountField;
import xyz.redtorch.pb.CoreField.OrderField;

/**
 * 模拟账户，用于对真实行情进行模拟交易，便于验证策略可行性
 * @author kevinhuangwl
 *
 */
public class SimulateAccount extends BaseAccount{

	@Override
	public boolean placeOrder() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean cancelOrder() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AccountField getAccountInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrderField getOrderInfo() {
		// TODO Auto-generated method stub
		return null;
	}

}
