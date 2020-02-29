package tech.xuanwu.northstar.core.persistence.repo;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.mongodb.client.model.Sorts;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.CommonConstant;
import tech.xuanwu.northstar.entity.AccountInfo;
import xyz.redtorch.common.mongo.MongoDBClient;

@Slf4j
@Repository
public class AccountRepoImpl implements AccountRepo{

	@Autowired
	MongoDBClient mongodb;
	
	final String DB = "DB_ADMIN";
	
	final String TBL_ACCOUNT = "Account";

	Gson gson = new Gson();
	
	@Override
	public boolean upsert(AccountInfo account) throws IllegalArgumentException, IllegalAccessException {
		log.info("插入账户信息");
		String tradingDay = LocalDate.now().format(CommonConstant.D_FORMAT_INT_FORMATTER);
		if(StringUtils.isEmpty(account.getTradingDay())) {			
			account.setTradingDay(tradingDay);
		}
		Document doc = Document.parse(gson.toJson(account));
		Document filter = new Document().append("tradingDay", tradingDay);
		return mongodb.upsert(DB, TBL_ACCOUNT, doc, filter);
	}

	@Override
	public AccountInfo getLatestAccountInfoByName(String accountName) {
		log.info("查询账户-[{}] 最近交易日的账户信息", accountName);
		String tradingDay = LocalDate.now().format(CommonConstant.D_FORMAT_INT_FORMATTER);
		Document filter = new Document()
				.append("tradingDay", tradingDay)
				.append("name", accountName);
		Sorts.descending("tradingDay");
		List<Document> resultList = mongodb.find(DB, TBL_ACCOUNT, filter, Sorts.descending("tradingDay"));
		if(resultList.size()==0) {
			return null;
		}
		return gson.fromJson(resultList.get(0).toJson(), AccountInfo.class);
	}
	
}
