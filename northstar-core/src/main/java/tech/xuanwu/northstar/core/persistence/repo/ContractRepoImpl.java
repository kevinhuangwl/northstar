package tech.xuanwu.northstar.core.persistence.repo;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import static com.mongodb.client.model.Filters.*;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.CommonConstant;
import tech.xuanwu.northstar.entity.ContractInfo;
import xyz.redtorch.common.mongo.MongoDBClient;

@Slf4j
@Repository
public class ContractRepoImpl implements ContractRepo{
	
	@Autowired
	MongoDBClient mongodb;
	
	final String DB = "DB_ADMIN";
	
	final String TBL_CONTRACT = "Contracts";
	
	Gson gson = new Gson();

	@Override
	public boolean upsert(ContractInfo contract) throws IllegalArgumentException, IllegalAccessException {
		log.info("插入合约");
		Document doc = Document.parse(gson.toJson(contract));
		return mongodb.upsert(DB, TBL_CONTRACT, doc, doc);
	}

	@Override
	public List<ContractInfo> getAllSubscribedContracts() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<Document> subscribedContracts = mongodb.find(DB, TBL_CONTRACT, 
				and(eq("isSubscribed", true), gte("lastTradeDateOrContractMonth", LocalDate.now().format(CommonConstant.D_FORMAT_INT_FORMATTER))));
		log.info("获取订阅合约 {} 个", subscribedContracts.size());
		List<ContractInfo> resultList = new ArrayList<>(subscribedContracts.size());
		for(Document doc : subscribedContracts) {
			ContractInfo info = gson.fromJson(doc.toJson(), ContractInfo.class);
			resultList.add(info);
		}
		return resultList;
	}

	@Override
	public boolean delete(ContractInfo contract) {
		log.info("移除合约");
		Document filter = Document.parse(gson.toJson(contract));
		return mongodb.delete(DB, TBL_CONTRACT, filter);
	}

	@Override
	public List<ContractInfo> getAllAvailableContracts() throws Exception {
		List<Document> mkContracts = mongodb.find(DB, TBL_CONTRACT, 
				gte("lastTradeDateOrContractMonth", LocalDate.now().format(CommonConstant.D_FORMAT_INT_FORMATTER)));
		log.info("获取市场合约{}个", mkContracts.size());
		List<ContractInfo> resultList = new ArrayList<>(mkContracts.size());
		for(Document doc : mkContracts) {
			ContractInfo info = gson.fromJson(doc.toJson(), ContractInfo.class);
			resultList.add(info);
		}
		return resultList;
	}

	@Override
	public boolean insertIfAbsent(ContractInfo contract) throws Exception {
		Document filter = new Document()
				.append("contractId", contract.getContractId());
		List<Document> result = mongodb.find(DB, TBL_CONTRACT, filter);
		if(result.size()>0) {
			log.debug("合约{}已存在", contract.getContractId());
			return false;
		}
		
		log.info("新增合约");
		Document doc = Document.parse(gson.toJson(contract));
		return mongodb.insert(DB, TBL_CONTRACT, doc);
	}

	@Override
	public ContractInfo getContractBySymbol(String symbol) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
