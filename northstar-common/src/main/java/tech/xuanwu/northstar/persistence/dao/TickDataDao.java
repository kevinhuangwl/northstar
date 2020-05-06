package tech.xuanwu.northstar.persistence.dao;

import java.time.LocalDateTime;

import tech.xuanwu.northstar.entity.MinTickInfo;
import xyz.redtorch.pb.CoreField.TickField;

public interface TickDataDao {

	void saveTickData(MinTickInfo minTick);
	
	MinTickInfo loadTickData(String contractId, String tradingDay, String time);
	
	TickField[] loadTickData(String contractId, LocalDateTime startTime, LocalDateTime endTime);
}
