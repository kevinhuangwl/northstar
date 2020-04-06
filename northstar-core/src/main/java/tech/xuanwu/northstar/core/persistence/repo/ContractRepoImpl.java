package tech.xuanwu.northstar.core.persistence.repo;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.regex;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;

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
	
	final String TBL_INDEX_CONTRACT = "IndexContracts";
	
	Gson gson = new Gson();

	@Override
	public List<ContractInfo> getAllSubscribedContracts(String gatewayId) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<Document> subscribedContracts = mongodb.find(DB, TBL_CONTRACT, 
				and(eq("isSubscribed", true),
					eq("gatewayId", gatewayId),
					gte("lastTradeDateOrContractMonth", LocalDate.now().format(CommonConstant.D_FORMAT_INT_FORMATTER))));
		log.info("获取订阅合约 {} 个", subscribedContracts.size());
		List<ContractInfo> resultList = new ArrayList<>(subscribedContracts.size());
		for(Document doc : subscribedContracts) {
			ContractInfo info = gson.fromJson(doc.toJson(), ContractInfo.class);
			resultList.add(info);
		}
		return resultList;
	}

	@Override
	public boolean delete(String gatewayId, String symbol) {
		log.info("移除合约[{}_{}]", gatewayId, symbol);
		return mongodb.delete(DB, TBL_CONTRACT, new Document().append("symbol", symbol).append("gatewayId", gatewayId));
	}

	@Override
	public List<ContractInfo> getAllAvailableContracts(String gatewayId) throws Exception {
		List<Document> mkContracts = mongodb.find(DB, TBL_CONTRACT, 
				and(eq("gatewayId", gatewayId),
					gte("lastTradeDateOrContractMonth", LocalDate.now().format(CommonConstant.D_FORMAT_INT_FORMATTER))));
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
	public ContractInfo getContractBySymbol(String gatewayId, String symbol) throws Exception {
		List<Document> result = mongodb.find(DB, TBL_CONTRACT, and(eq("gatewayId", gatewayId), eq("symbol",symbol)));
		if(result.size()==0) {
			return null;
		}
		return gson.fromJson(result.get(0).toJson(), ContractInfo.class);
	}

	@Override
	public boolean updateById(ContractInfo contract) throws Exception {
		Document doc = Document.parse(gson.toJson(contract));
		return mongodb.updateOne(DB, TBL_CONTRACT, new Document("contractId", contract.getContractId()), doc);
	}

	@Override
	public List<ContractInfo> getSeriesContractsByExample(String gatewayId, String symbol) throws Exception {
		String contractName = symbol.replaceAll("\\d+$", "");
		List<Document> seriesContracts = mongodb.find(DB, TBL_CONTRACT, 
				and(eq("gatewayId", gatewayId),
					regex("symbol", contractName, "i"),
					gte("lastTradeDateOrContractMonth", LocalDate.now().format(CommonConstant.D_FORMAT_INT_FORMATTER))));
		log.info("查询【{}】品种的全月份合约，共{}个", contractName, seriesContracts.size());
		List<ContractInfo> resultList = new ArrayList<ContractInfo>(seriesContracts.size());
		for(Document doc : seriesContracts) {
			ContractInfo info = gson.fromJson(doc.toJson(), ContractInfo.class);
			resultList.add(info);
		}
		return resultList;
	}

}
