package tech.xuanwu.northstar.core.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * MongoDB配置类
 * @author kevinhuangwl
 *
 */

@Getter
@Setter
@ConfigurationProperties(prefix="db.mongo")
public class MongoDBSettings {

	private String host;
	
	private int port;
	
	private String username;
	
	private String password;
	
	private String auth;
}
