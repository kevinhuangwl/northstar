package tech.xuanwu.northstar.db;

import java.util.List;

import xyz.redtorch.common.mongo.MongoDBClient;

public class MongoDBClientAdapter implements DBClient{
	
	private MongoDBClient client;
	
	public MongoDBClientAdapter(MongoDBClient mongodbClient) {
		this.client = mongodbClient;
	}

	@Override
	public boolean insert(String db, String table, Object entity) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean insertMany(String db, String table, List<Object> listOfEntities) {
		// TODO Auto-generated method stub
		return false;
	}

}
