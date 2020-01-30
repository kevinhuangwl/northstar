package tech.xuanwu.northstar.core.config;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import tech.xuanwu.northstar.core.config.props.CtpGatewaySettingProperties;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableConfigurationProperties(CtpGatewaySettingProperties.class)
public class TestCtpGatewaySettingProperties {

	@Autowired
	CtpGatewaySettingProperties p;
	
	@Test
	public void test() {
		assertNotNull(p);
		assertNotNull(p.getGatewayImplClassName());
		assertNotNull(p.getUserID());
		
	}
}
