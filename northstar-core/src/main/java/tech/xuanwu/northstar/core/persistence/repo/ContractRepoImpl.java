package tech.xuanwu.northstar.core.persistence.repo;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import xyz.redtorch.common.mongo.MongoDBClient;
import xyz.redtorch.common.mongo.MongoDBUtils;
import xyz.redtorch.pb.CoreField.ContractField;

@Slf4j
@Repository
public class ContractRepoImpl implements ContractRepo{
	
	@Autowired
	MongoDBClient mongodb;
	
	final String DB = "DB_ADMIN";
	
	final String TBL_SUBSCRIBE_CONTRACT = "SubscribeContract";

	@Override
	public boolean upsert(ContractField contract) throws IllegalArgumentException, IllegalAccessException {
		log.info("插入订阅合约");
		Document doc = MongoDBUtils.beanToDocument(contract.toBuilder());
		return mongodb.upsert(DB, TBL_SUBSCRIBE_CONTRACT, doc, doc);
	}

	@Override
	public List<ContractField> getAllSubscribeContracts() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<Document> subscribeContracts = mongodb.find(DB, TBL_SUBSCRIBE_CONTRACT);
		log.info("获取订阅合约 {} 个", subscribeContracts.size());
		List<ContractField> resultList = new ArrayList<>(subscribeContracts.size());
		for(Document doc : subscribeContracts) {
			ContractField.Builder cfb = ContractField.newBuilder();
			MongoDBUtils.documentToBean(doc, cfb);
			resultList.add(cfb.build());
		}
		return resultList;
	}

}
