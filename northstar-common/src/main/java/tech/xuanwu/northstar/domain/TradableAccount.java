package tech.xuanwu.northstar.domain;

import java.time.LocalDate;
import java.util.List;

import tech.xuanwu.northstar.entity.OrderInfo;
import tech.xuanwu.northstar.entity.PositionInfo;
import tech.xuanwu.northstar.entity.TransactionInfo;
import xyz.redtorch.pb.CoreField.CancelOrderReqField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;

public interface TradableAccount {

	void submitOrder(SubmitOrderReqField submitOrderReq);
	
	void cancelOrder(CancelOrderReqField cancelOrderReq);
	
	void updatePosition(PositionInfo position);
	
	void updateOrder(OrderInfo order);

	void updateTransaction(TransactionInfo transaction);
	
	List<PositionInfo> getPositionInfoList();
	
	List<OrderInfo> getOrderInfoList(LocalDate fromDate, LocalDate toDate);
	
	List<TransactionInfo> getTransactionInfoList(LocalDate fromDate, LocalDate toDate);
}
