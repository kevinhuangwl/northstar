package tech.xuanwu.northstar.strategy.client.dao;

import java.time.LocalDateTime;

import xyz.redtorch.pb.CoreField.BarField;

public interface MarketDataDao {

	BarField[] loadBarDataByMin(String contractId, LocalDateTime startTime, LocalDateTime endTime);
	
	BarField[] loadBarDataByDay(String contractId, LocalDateTime startTime, LocalDateTime endTime);
}
