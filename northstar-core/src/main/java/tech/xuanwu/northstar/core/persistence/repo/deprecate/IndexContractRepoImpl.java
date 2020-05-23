package tech.xuanwu.northstar.core.persistence.repo.deprecate;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.core.persistence.repo.IndexContractRepo;
import tech.xuanwu.northstar.entity.ContractInfo;
import xyz.redtorch.common.mongo.MongoDBClient;

@Slf4j
@Repository
public class IndexContractRepoImpl implements IndexContractRepo{

	@Autowired
	MongoDBClient mongodb;
	
	final String DB = "DB_ADMIN";
	
	final String TBL_INDEX_CONTRACT = "IndexContracts";
	
	Gson gson = new Gson();
	
	@Override
	public boolean insertIfAbsent(ContractInfo contract) throws Exception {
		Document filter = new Document()
				.append("contractId", contract.getContractId());
		List<Document> result = mongodb.find(DB, TBL_INDEX_CONTRACT, filter);
		if(result.size()>0) {
			log.debug("合约{}已存在", contract.getContractId());
			return false;
		}
		
		log.info("新增合约");
		Document doc = Document.parse(gson.toJson(contract));
		return mongodb.insert(DB, TBL_INDEX_CONTRACT, doc);
	}

	@Override
	public boolean delete(String gatewayId, String symbol) {
		log.info("移除合约[{}_{}]", gatewayId, symbol);
		return mongodb.delete(DB, TBL_INDEX_CONTRACT, new Document().append("symbol", symbol).append("gatewayId", gatewayId));
	}

	@Override
	public List<ContractInfo> getAllSubscribedContracts(String gatewayId) throws Exception {
		List<Document> subscribedContracts = mongodb.find(DB, TBL_INDEX_CONTRACT, 
				and(eq("isSubscribed", true),
					eq("gatewayId", gatewayId)));
		log.info("获取指数订阅合约 {} 个", subscribedContracts.size());
		List<ContractInfo> resultList = new ArrayList<>(subscribedContracts.size());
		for(Document doc : subscribedContracts) {
			ContractInfo info = gson.fromJson(doc.toJson(), ContractInfo.class);
			resultList.add(info);
		}
		return resultList;
	}

}
