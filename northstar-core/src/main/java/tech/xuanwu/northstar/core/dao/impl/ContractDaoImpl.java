package tech.xuanwu.northstar.core.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.core.dao.ContractDao;
import xyz.redtorch.common.mongo.MongoDBClient;
import xyz.redtorch.common.mongo.MongoDBUtils;
import xyz.redtorch.pb.CoreField.ContractField;

@Slf4j
@Repository
public class ContractDaoImpl implements ContractDao{
	
	@Autowired
	MongoDBClient mongodb;
	
	final String DB = "DB_ADMIN";
	
	final String TBL_SUBSCRIBE_CONTRACT = "SubscribeContract";

	@Override
	public List<ContractField> getSubscribeContracts() {
		List<Document> subscribeContracts = mongodb.find(DB, TBL_SUBSCRIBE_CONTRACT);
		List<ContractField> resultList = new ArrayList<>(subscribeContracts.size());
		for(Document doc : subscribeContracts) {
			ContractField.Builder cfb = ContractField.newBuilder();
			try {
				MongoDBUtils.documentToBean(doc, cfb);
				resultList.add(cfb.build());
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				log.error("合约对象转换异常", e);
			}
		}
		return resultList;
	}

	@Override
	public void upsertSubscribeContract(ContractField contract) {
		try {
			Document doc = MongoDBUtils.beanToDocument(contract.toBuilder());
			mongodb.upsert(DB, TBL_SUBSCRIBE_CONTRACT, doc, doc);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			log.error("合约对象转换异常", e);
		}
		
	}

}
