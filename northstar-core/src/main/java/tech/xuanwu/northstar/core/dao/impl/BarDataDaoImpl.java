package tech.xuanwu.northstar.core.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.client.model.Filters;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.core.dao.BarDataDao;
import xyz.redtorch.common.mongo.MongoDBClient;
import xyz.redtorch.common.mongo.MongoDBUtils;
import xyz.redtorch.common.util.CommonUtils;
import xyz.redtorch.pb.CoreField.BarField;

@Slf4j
@Repository
public class BarDataDaoImpl implements BarDataDao{

	@Autowired
	MongoDBClient mongodb;
	
	final String DB_BAR = "DB_BAR_DATA";
	
	final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmssSSS");
	
	@Override
	public boolean saveBarData(BarField bar) {
		String contractId = bar.getContract().getContractId();
		try {
			Document doc = MongoDBUtils.beanToDocument(bar.toBuilder());
			mongodb.upsert(DB_BAR, contractId, doc, doc);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			log.error("类型转换异常", e);
			return false;
		}
		return true;
	}

	@Override
	public BarField[] loadBarData(String contractId, LocalDateTime startTime, LocalDateTime endTime) {
		long startTimestamp = CommonUtils.localDateTimeToMills(startTime);
		long endTimestamp = CommonUtils.localDateTimeToMills(endTime);
		
		List<Document> result = mongodb.find(DB_BAR, contractId, Filters.and(Filters.gte("actionTimestamp_", startTimestamp), Filters.lte("actionTimestamp_", endTimestamp)));
		BarField[] bars = new BarField[result.size()];
		for(int i=0;i<result.size();i++) {
			try {
				BarField.Builder bfb = BarField.newBuilder();
				bfb = MongoDBUtils.documentToBean(result.get(i), bfb);
				bars[i] = bfb.build();
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				log.error("类型转换异常", e);
				break;
			}
		}
		return bars;
	}
	
}
