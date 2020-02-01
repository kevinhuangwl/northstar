package tech.xuanwu.northstar.core.dao.impl;

import java.time.LocalDateTime;

import tech.xuanwu.northstar.core.dao.TransactionDao;
import xyz.redtorch.pb.CoreField.TradeField;

public class TransactionDaoImpl implements TransactionDao{

	@Override
	public boolean insert(TradeField tradeField) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TradeField[] getTransactionRecordsByPeriod(String accountId, LocalDateTime startTime,
			LocalDateTime endTime) {
		// TODO Auto-generated method stub
		return null;
	}

}
