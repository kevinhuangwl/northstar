package tech.xuanwu.northstar.core.dao.impl;

import java.time.LocalDateTime;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.core.dao.TickDataDao;
import tech.xuanwu.northstar.entity.Tick;
import tech.xuanwu.northstar.entity.TickFactory;
import xyz.redtorch.common.mongo.MongoDBClient;
import xyz.redtorch.common.mongo.MongoDBUtils;
import xyz.redtorch.pb.CoreField.TickField;

@Slf4j
@Repository
public class TickDataDaoImpl implements TickDataDao{

	@Autowired
	MongoDBClient mongodb;
	
	final String DB_TICK = "DB_TICK_DATA";
	
	
	@Override
	public boolean saveTickData(TickField tickField) {
		String contractId = tickField.getContract().getContractId();
		Tick tick = TickFactory.getTickAs(tickField);
		try {
			Document doc = MongoDBUtils.beanToDocument(tick);
			mongodb.upsert(DB_TICK, contractId, doc, doc);
			//释放Tick对象实现对象重用，减少GC
			TickFactory.releaseTick(tick);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			log.error("类型转换异常", e);
			return false;
		}
		return true;
	}

	@Override
	public TickField[] loadTickData(String contractId, LocalDateTime startTime, LocalDateTime endTime) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
