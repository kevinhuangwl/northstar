package tech.xuanwu.northstar.core.config.props;

import lombok.Getter;
import lombok.Setter;

/**
 * MongoDB配置类
 * @author kevinhuangwl
 *
 */

@Getter
@Setter
public class MongoDBProperties {

	private String host;
	
	private int port;
	
	private String username;
	
	private String password;
	
	private String auth;
}
