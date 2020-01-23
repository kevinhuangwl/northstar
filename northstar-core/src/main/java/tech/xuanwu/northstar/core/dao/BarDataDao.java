package tech.xuanwu.northstar.core.dao;

import java.time.LocalDateTime;

import xyz.redtorch.pb.CoreField.BarField;

public interface BarDataDao {

	boolean saveBarData(BarField bar);
	
	BarField[] loadBarData(String contractId, LocalDateTime startTime, LocalDateTime endTime);
}
