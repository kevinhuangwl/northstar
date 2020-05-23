package tech.xuanwu.northstar.core.persistence.repo.deprecate;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.mongodb.client.model.Filters;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.core.persistence.repo.GatewayRepo;
import tech.xuanwu.northstar.entity.GatewayInfo;
import xyz.redtorch.common.mongo.MongoDBClient;

@Slf4j
@Repository
public class GatewayRepoImpl implements GatewayRepo{

	@Autowired
	MongoDBClient mongodb;
	
	final String DB = "DB_ADMIN";
	
	final String TBL_GATEWAY = "Gateways";
	
	Gson gson = new Gson();
	
	@Override
	public boolean upsertById(GatewayInfo gateway) {
		log.info("插入网关信息");
		Document doc = Document.parse(gson.toJson(gateway));
		return mongodb.upsert(DB, TBL_GATEWAY, doc, new Document().append("gatewayId", gateway.getGatewayId()));
	}

	@Override
	public GatewayInfo findGatewayById(String gatewayId) {
		log.info("查询网关信息");
		List<Document> result = mongodb.find(DB, TBL_GATEWAY, Filters.eq("gatewayId", gatewayId));
		if(result.size()==0) {
			return null;
		}
		return gson.fromJson(result.get(0).toJson(), GatewayInfo.class);
	}

	@Override
	public List<GatewayInfo> getAllGateways() {
		log.info("查询全部网关信息");
		List<Document> results = mongodb.find(DB, TBL_GATEWAY);
		List<GatewayInfo> resultList = new ArrayList<>(results.size());
		for(Document d : results) {
			resultList.add(gson.fromJson(d.toJson(), GatewayInfo.class));
		}
		return resultList;
	}

}
