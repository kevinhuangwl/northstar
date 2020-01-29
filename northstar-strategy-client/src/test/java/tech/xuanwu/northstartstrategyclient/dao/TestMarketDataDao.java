package tech.xuanwu.northstartstrategyclient.dao;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestMarketDataDao {

	@Autowired
	MongoTemplate mongoTemplate;
	
	@Before
	public void before() {
		assertNotNull(mongoTemplate);
	}
	
	@After
	public void after() {}
	
	@Test
	public void testFindByMin() {}
	
	@Test
	public void testFindByDay() {}
	
}
