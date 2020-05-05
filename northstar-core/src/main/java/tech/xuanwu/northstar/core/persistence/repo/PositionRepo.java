package tech.xuanwu.northstar.core.persistence.repo;

import java.util.List;

import tech.xuanwu.northstar.entity.PositionInfo;

public interface PositionRepo {

	boolean upsertById(PositionInfo p);
	
	boolean removeById(PositionInfo p);
	
	List<PositionInfo> getPositionListByAccountId(String accountId);
}
