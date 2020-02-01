package tech.xuanwu.northstar.core.dao.impl;

import java.time.LocalDate;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.QueryBuilder;
import com.mongodb.client.model.Filters;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.core.dao.AccountDao;
import xyz.redtorch.common.mongo.MongoDBClient;
import xyz.redtorch.common.mongo.MongoDBUtils;
import xyz.redtorch.pb.CoreField.AccountField;

@Slf4j
@Repository
public class AccountDaoImpl implements AccountDao{
	
	@Autowired
	MongoDBClient mongodb;
	
	final String DB = "DB_ACCOUNT";
	
	final String COLLECTION = "Account";

	@Override
	public boolean insert(AccountField account) {
		try {
			Document doc = MongoDBUtils.beanToDocument(account);
			mongodb.insert(DB, COLLECTION, doc);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			log.error("账户信息插入异常", e);
			return false;
		}
		return true;
	}

	@Override
	public AccountField getLatestRecord(String accountId) {
		
//		mongodb.find(DB, COLLECTION, );
		return null;
	}

	@Override
	public AccountField[] getRecordsByPeriod(String accountId, LocalDate startDate, LocalDate endDate) {
		// TODO Auto-generated method stub
		return null;
	}

}
