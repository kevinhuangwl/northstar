package tech.xuanwu.northstar.persistence.dao;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.mongodb.client.model.Filters;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.entity.DayBarInfo;
import xyz.redtorch.common.mongo.MongoDBClient;
import xyz.redtorch.common.mongo.MongoDBUtils;
import xyz.redtorch.common.util.CommonUtils;
import xyz.redtorch.pb.CoreField.BarField;

@Slf4j
@Repository
public class BarDataDaoImpl implements BarDataDao{

	@Autowired
	MongoDBClient mongodb;
	
	final String DB_BAR = "DB_BAR_DATA";
	
	final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmssSSS");
	
	Gson gson = new Gson();
	
	@Override
	public void saveBarData(DayBarInfo bar) {
		Document doc = Document.parse(gson.toJson(bar));
		mongodb.upsert(DB_BAR, bar.getUnifiedSymbol(), doc, new Document().append("unifiedSymbol", bar.getUnifiedSymbol()).append("tradingDay", bar.getTradingDay()));
	}

	@Override
	public DayBarInfo loadBarData(String contractId, String tradingDay) {
		List<Document> result = mongodb.find(DB_BAR, contractId, Filters.eq("tradingDay", tradingDay));
		if(result.size() == 0) {
			return null;
		}
		JsonWriterSettings settings = JsonWriterSettings.builder()
		         .int64Converter((value, writer) -> writer.writeNumber(value.toString()))
		         .build();
		return gson.fromJson(result.get(0).toJson(settings), DayBarInfo.class);
	}
	
	@Override
	public BarField[] loadBarData(String contractId, LocalDateTime startTime, LocalDateTime endTime) {
		return null;
	}

}
