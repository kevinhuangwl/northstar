package tech.xuanwu.northstar.domain;

import java.time.LocalDate;
import java.util.List;

import xyz.redtorch.pb.CoreField.CancelOrderReqField;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.PositionField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;
import xyz.redtorch.pb.CoreField.TradeField;

public interface Tradable {

	String getName();
	
	double getBalance();
	
	double getMargin();
	
	void submitOrder(SubmitOrderReqField submitOrderReq);
	
	void cancelOrder(CancelOrderReqField cancelOrderReq);
	
	void updatePosition(PositionField position);
	
	void updateOrder(OrderField order);

	void updateTransaction(TradeField transaction);
	
	List<PositionField> getPositionInfoList();
	
	List<OrderField> getOrderInfoList(LocalDate fromDate, LocalDate toDate);
	
	List<TradeField> getTransactionInfoList(LocalDate fromDate, LocalDate toDate);
}
