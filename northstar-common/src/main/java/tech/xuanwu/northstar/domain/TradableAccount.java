package tech.xuanwu.northstar.domain;

import java.time.LocalDate;
import java.util.List;

import tech.xuanwu.northstar.entity.OrderInfo;
import tech.xuanwu.northstar.entity.PositionInfo;
import tech.xuanwu.northstar.entity.TransactionInfo;
import tech.xuanwu.northstar.exception.TradeException;
import xyz.redtorch.pb.CoreField.CancelOrderReqField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;

public interface TradableAccount {

	void submitOrder(SubmitOrderReqField submitOrderReq) throws TradeException;
	
	void cancelOrder(CancelOrderReqField cancelOrderReq) throws TradeException;
	
	void updatePosition(PositionInfo position);
	
	void updateOrder(OrderInfo order);

	void updateTransaction(TransactionInfo transaction);
	
	List<PositionInfo> getPositionInfoList();
	
	List<OrderInfo> getOrderInfoList(LocalDate fromDate, LocalDate toDate);
	
	List<TransactionInfo> getTransactionInfoList(LocalDate fromDate, LocalDate toDate);
}
