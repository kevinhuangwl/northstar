package tech.xuanwu.northstar.strategy.client.dao.impl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import tech.xuanwu.northstar.strategy.client.dao.MarketDataDao;
import xyz.redtorch.common.util.CommonUtils;
import xyz.redtorch.pb.CoreField.BarField;
import xyz.redtorch.pb.CoreField.BarField.Builder;

@Repository
public class MarketDataDaoImpl implements MarketDataDao {
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	final String DB_BAR = "DB_BAR_DATA";

	@Override
	public BarField[] loadBarDataByMin(String contractId, LocalDateTime startTime, LocalDateTime endTime) {
		long startTimestamp = CommonUtils.localDateTimeToMills(startTime);
		long endTimestamp = CommonUtils.localDateTimeToMills(endTime);
		
		List<Builder> builders = mongoTemplate.find(Query.query(Criteria.where("actionTimestamp_").gte(startTimestamp).and("actionTimestamp_").lte(endTimestamp)), 
				BarField.Builder.class, contractId);

		return convertToBarField(builders);
	}

	@Override
	public BarField[] loadBarDataByDay(String contractId, LocalDateTime startTime, LocalDateTime endTime) {
		long startTimestamp = CommonUtils.localDateTimeToMills(startTime);
		long endTimestamp = CommonUtils.localDateTimeToMills(endTime);

//		mongoTemplate.a
//		return convertToBarField(builders);
		return null;
	}
	
	private BarField[] convertToBarField(List<Builder> builders) {
		BarField[] bars = new BarField[builders.size()];
		for(int i=0;i<builders.size();i++) {
			bars[i] = builders.get(i).build();
		}
		
		return bars;
		
	}

}
