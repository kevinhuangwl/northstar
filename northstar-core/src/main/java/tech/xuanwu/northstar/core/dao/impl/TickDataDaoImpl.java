package tech.xuanwu.northstar.core.dao.impl;

import java.time.LocalDateTime;

import tech.xuanwu.northstar.core.dao.TickDataDao;
import xyz.redtorch.pb.CoreField.TickField;

public class TickDataDaoImpl implements TickDataDao{

	
	@Override
	public boolean saveTickData(TickField tick) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TickField[] loadTickData(String unifiedSymbol, LocalDateTime startTime, LocalDateTime endTime) {
		// TODO Auto-generated method stub
		return null;
	}

}
