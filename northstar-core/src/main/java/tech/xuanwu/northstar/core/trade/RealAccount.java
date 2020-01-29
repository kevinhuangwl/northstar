package tech.xuanwu.northstar.core.trade;

import org.springframework.stereotype.Service;

import xyz.redtorch.pb.CoreField.AccountField;
import xyz.redtorch.pb.CoreField.OrderField;

/**
 * 真实账户，对接真实网关账户
 * @author kevinhuangwl
 *
 */
@Service
public class RealAccount extends BaseAccount{

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
