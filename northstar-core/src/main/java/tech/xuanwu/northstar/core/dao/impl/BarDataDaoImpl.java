package tech.xuanwu.northstar.core.dao.impl;

import java.time.LocalDateTime;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.core.dao.BarDataDao;
import xyz.redtorch.common.mongo.MongoDBClient;
import xyz.redtorch.common.mongo.MongoDBUtils;
import xyz.redtorch.pb.CoreField.BarField;

@Slf4j
@Repository
public class BarDataDaoImpl implements BarDataDao{

	@Autowired
	MongoDBClient mongodb;
	
	final String DB_BAR = "DB_BAR_DATA";
	
	@Override
	public boolean saveBarData(BarField bar) {
		String contractId = bar.getContract().getContractId();
		try {
			Document doc = MongoDBUtils.beanToDocument(bar);
			mongodb.upsert(DB_BAR, contractId, doc, null);
			
		} catch (IllegalArgumentException | IllegalAccessException e) {
			log.error("类型转换异常", e);
			return false;
		}
		return true;
	}

	@Override
	public BarField[] loadBarData(String contractId, LocalDateTime startTime, LocalDateTime endTime) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
