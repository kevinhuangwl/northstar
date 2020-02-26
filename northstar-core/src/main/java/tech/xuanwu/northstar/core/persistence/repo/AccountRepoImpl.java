package tech.xuanwu.northstar.core.persistence.repo;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.entity.AccountInfo;
import xyz.redtorch.common.mongo.MongoDBClient;
import xyz.redtorch.common.mongo.MongoDBUtils;

@Slf4j
@Repository
public class AccountRepoImpl implements AccountRepo{

	@Autowired
	MongoDBClient mongodb;
	
	final String DB = "DB_ADMIN";
	
	final String TBL_ACCOUNT = "Account";

	
	@Override
	public boolean upsert(AccountInfo account) throws IllegalArgumentException, IllegalAccessException {
		log.info("插入账户信息");
		Document doc = MongoDBUtils.beanToDocument(account);
		return mongodb.insert(DB, TBL_ACCOUNT, doc);
	}

	@Override
	public AccountInfo getLatestAccountInfoByName(String accountName) {
		// TODO Auto-generated method stub
		return null;
	}

}
