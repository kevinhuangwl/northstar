package tech.xuanwu.northstar.core.persistence.repo;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.mongodb.client.model.Filters;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.entity.PositionInfo;
import xyz.redtorch.common.mongo.MongoDBClient;

@Slf4j
@Repository
public class PositionRepoImpl implements PositionRepo{

	@Autowired
	MongoDBClient mongodb;
	
	final String DB = "DB_ADMIN";
	
	final String TBL_POSITION = "Positions";
	
	Gson gson = new Gson();
	
	@Override
	public boolean upsertById(PositionInfo p) {
		log.info("插入持仓信息");
		Document doc = Document.parse(gson.toJson(p));
		return mongodb.upsert(DB, TBL_POSITION, doc, new Document().append("positionId", p.getPositionId()));
	}

	@Override
	public List<PositionInfo> getPositionListByAccountId(String accountId) {
		log.info("根据网关[{}]查询相关持仓列表", accountId);
		List<Document> results = mongodb.find(DB, TBL_POSITION, Filters.eq("accountId", accountId));
		List<PositionInfo> resultList = new ArrayList<>(results.size());
		for(Document d : results) {
			resultList.add(gson.fromJson(d.toJson(), PositionInfo.class));
		}
		return resultList;
	}

	@Override
	public boolean removeById(PositionInfo p) {
		log.info("移除持仓信息");
		return mongodb.delete(DB, TBL_POSITION, new Document().append("positionId", p.getPositionId()));
	}

}
