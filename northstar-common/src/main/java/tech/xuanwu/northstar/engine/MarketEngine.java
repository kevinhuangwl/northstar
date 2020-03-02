package tech.xuanwu.northstar.engine;

import xyz.redtorch.pb.CoreField.CancelOrderReqField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;
import xyz.redtorch.pb.CoreField.TickField;

public interface MarketEngine {

	void submitOrder(SubmitOrderReqField submitOrderField);
	
	void cancelOrder(CancelOrderReqField cancelOrderField);
	
	void updateTick(TickField tickField);
	
	void deposit(double money);
	
	void withdraw(double money);
}
