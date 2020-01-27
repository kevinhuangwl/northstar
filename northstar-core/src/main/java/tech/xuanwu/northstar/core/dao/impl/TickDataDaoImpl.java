package tech.xuanwu.northstar.core.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.client.model.Filters;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.core.dao.TickDataDao;
import xyz.redtorch.common.mongo.MongoDBClient;
import xyz.redtorch.common.mongo.MongoDBUtils;
import xyz.redtorch.pb.CoreField.BarField;
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
		long startTimestamp = startTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
		long endTimestamp = endTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
		
		List<Document> result = mongodb.find(DB_TICK, contractId, Filters.and(Filters.gte("actionTimestamp_", startTimestamp), Filters.lte("actionTimestamp_", endTimestamp)));
		TickField[] ticks = new TickField[result.size()];
		for(int i=0;i<result.size();i++) {
			try {
				TickField.Builder tfb = TickField.newBuilder();
				tfb = MongoDBUtils.documentToBean(result.get(i), tfb);
				ticks[i] = tfb.build();
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				log.error("类型转换异常", e);
				break;
			}
		}
		return ticks;
	}

	
}
