package tech.xuanwu.northstar.core.mongo;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import xyz.redtorch.pb.CoreField.TickField;

@SpringBootTest
public class TestMongoDBUtil {

	
	@Test
	public void testBeanToDocument() {
		TickField.Builder tickBuilder = TickField.newBuilder();
	}
	
	@Test
	public void testDocumentToBean() {
		
	}
}
