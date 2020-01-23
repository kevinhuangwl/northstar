package tech.xuanwu.northstar.db;

import java.util.List;

public interface DBClient {

	
	boolean insert(String db, String table, Object entity);
	
	boolean insertMany(String db, String table, List<Object> listOfEntities);
	
}
