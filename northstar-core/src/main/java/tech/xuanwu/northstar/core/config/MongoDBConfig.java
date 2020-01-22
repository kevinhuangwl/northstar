package tech.xuanwu.northstar.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import xyz.redtorch.common.mongo.MongoDBClient;

@Slf4j
@Configuration
public class MongoDBConfig {
	
	@Bean
	@ConfigurationProperties(prefix="db.mongo")
	public MongoDBProperties getMongoProps() {
		return new MongoDBProperties();
	}

	@Bean
	public MongoDBClient getMongoDBClient() {
		MongoDBProperties p = getMongoProps();
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
