package tech.xuanwu.northstar.core.persistence.repo;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.entity.ContractInfo;
import xyz.redtorch.common.mongo.MongoDBClient;

@Slf4j
@Repository
public class ContractRepoImpl implements ContractRepo{
	
	@Autowired
	MongoDBClient mongodb;
	
	final String DB = "DB_ADMIN";
	
	final String TBL_SUBSCRIBE_CONTRACT = "SubscribeContract";
	
	Gson gson = new Gson();

	@Override
	public boolean upsert(ContractInfo contract) throws IllegalArgumentException, IllegalAccessException {
		log.info("插入订阅合约");
		Document doc = Document.parse(gson.toJson(contract));
		return mongodb.upsert(DB, TBL_SUBSCRIBE_CONTRACT, doc, doc);
	}

	@Override
	public List<ContractInfo> getAllSubscribedContracts() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<Document> subscribeContracts = mongodb.find(DB, TBL_SUBSCRIBE_CONTRACT);
		log.info("获取订阅合约 {} 个", subscribeContracts.size());
		List<ContractInfo> resultList = new ArrayList<>(subscribeContracts.size());
		for(Document doc : subscribeContracts) {
			ContractInfo info = gson.fromJson(doc.toJson(), ContractInfo.class);
			resultList.add(info);
		}
		return resultList;
	}

}
