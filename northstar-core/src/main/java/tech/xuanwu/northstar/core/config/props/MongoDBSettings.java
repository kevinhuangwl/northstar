package tech.xuanwu.northstar.core.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * MongoDB配置类
 * @author kevinhuangwl
 *
 */

@Getter
@Setter
@ConfigurationProperties(prefix="spring.data.mongodb")
public class MongoDBSettings {

	private String host;
	
	private int port;
	
	private String username;
	
	private String password;
	
	private String auth;
}
