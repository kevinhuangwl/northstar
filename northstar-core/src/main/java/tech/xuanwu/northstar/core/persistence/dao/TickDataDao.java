package tech.xuanwu.northstar.core.persistence.dao;

import java.time.LocalDateTime;

import xyz.redtorch.pb.CoreField.TickField;

public interface TickDataDao {

	boolean saveTickData(TickField tick);
	
	TickField[] loadTickData(String contractId, LocalDateTime startTime, LocalDateTime endTime);
}
