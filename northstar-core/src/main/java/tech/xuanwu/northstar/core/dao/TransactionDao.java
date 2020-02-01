package tech.xuanwu.northstar.core.dao;

import java.time.LocalDateTime;

import xyz.redtorch.pb.CoreField.TradeField;

public interface TransactionDao {

	boolean insert(TradeField tradeField);
	
	TradeField[] getTransactionRecordsByPeriod(String accountId, LocalDateTime startTime, LocalDateTime endTime);
}
