package tech.xuanwu.northstar.domain;

import java.util.List;

import xyz.redtorch.pb.CoreField.CancelOrderReqField;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.PositionField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;
import xyz.redtorch.pb.CoreField.TradeField;

public interface IAccountLike {

	String getName();
	
	double getBalance();
	
	double getMargin();
	
	String placeOrder(SubmitOrderReqField submitOrderReq);
	
	boolean cancelOrder(CancelOrderReqField cancelOrderReq);
	
	void updatePosition(PositionField position);
	
	List<PositionField> getPositionList();
	
	void updateOrder(OrderField order);
	
	List<OrderField> getOrderListOfCurrentTradeDay();
	
	void updateTransaction(TradeField transaction);
	
	List<TradeField> getTransactionListOfCurrentTradeDay();
}
