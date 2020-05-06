package tech.xuanwu.northstar.persistence.dao;

import java.time.LocalDateTime;

import tech.xuanwu.northstar.entity.DayBarInfo;
import xyz.redtorch.pb.CoreField.BarField;

public interface BarDataDao {

	void saveBarData(DayBarInfo bar);
	
	DayBarInfo loadBarData(String contractId, String tradingDay);
	
	BarField[] loadBarData(String contractId, LocalDateTime startTime, LocalDateTime endTime);
}
