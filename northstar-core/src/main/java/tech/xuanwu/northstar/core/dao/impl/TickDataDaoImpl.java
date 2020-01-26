package tech.xuanwu.northstar.core.dao.impl;

import java.time.LocalDateTime;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.core.dao.TickDataDao;
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
		try {
			Document doc = MongoDBUtils.beanToDocument(tickField.toBuilder());
			mongodb.upsert(DB_TICK, contractId, doc, doc);
			//释放Tick对象实现对象重用，减少GC
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
