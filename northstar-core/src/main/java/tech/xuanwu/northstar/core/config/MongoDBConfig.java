package tech.xuanwu.northstar.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.core.config.props.MongoDBSettings;
import xyz.redtorch.common.mongo.MongoDBClient;

@Slf4j
@Configuration
public class MongoDBConfig {
	

	@Bean
	public MongoDBClient getMongoDBClient(MongoDBSettings p) {
		MongoDBClient mongoDBClient = null;
		try {
			mongoDBClient = new MongoDBClient(p.getHost(), p.getPort(), p.getUsername(), p.getPassword(), p.getAuth());
		} catch (Exception e) {
			log.error("数据库连接失败,程序终止", e);
			System.exit(0);
		}
		return mongoDBClient;
	}
}
