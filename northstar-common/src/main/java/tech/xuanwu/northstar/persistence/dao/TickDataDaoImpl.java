package tech.xuanwu.northstar.persistence.dao;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.List;

import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.mongodb.client.model.Filters;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.entity.MinTickInfo;
import xyz.redtorch.common.mongo.MongoDBClient;
import xyz.redtorch.common.mongo.MongoDBUtils;
import xyz.redtorch.common.util.CommonUtils;
import xyz.redtorch.pb.CoreField.TickField;

@Slf4j
@Repository
public class TickDataDaoImpl implements TickDataDao{

	@Autowired
	MongoDBClient mongodb;
	
	final String DB_TICK = "DB_TICK_DATA";
	
	Gson gson = new Gson();
	
	@Override
	public void saveTickData(MinTickInfo tick) {
		Document doc = Document.parse(gson.toJson(tick));
		mongodb.upsert(DB_TICK, tick.getUnifiedSymbol(), doc, new Document().append("unifiedSymbol", tick.getUnifiedSymbol()).append("actionTimeMin", tick.getActionTimeMin()));
	}
	
	@Override
	public MinTickInfo loadTickData(String contractId, String tradingDay, String time) {
		List<Document> result = mongodb.find(DB_TICK, contractId, Filters.and(Filters.eq("tradingDay", tradingDay), Filters.eq("actionTimeMin", time)));
		if(result.size() == 0) {
			return null;
		}
		JsonWriterSettings settings = JsonWriterSettings.builder()
		         .int64Converter((value, writer) -> writer.writeNumber(value.toString()))
		         .build();
		return gson.fromJson(result.get(0).toJson(settings), MinTickInfo.class);
	}

	@Override
	public TickField[] loadTickData(String contractId, LocalDateTime startTime, LocalDateTime endTime) {
		return null;
	}

	
}
