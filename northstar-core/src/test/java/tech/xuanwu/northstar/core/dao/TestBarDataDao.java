package tech.xuanwu.northstar.core.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import tech.xuanwu.northstar.core.dao.impl.BarDataDaoImpl;
import xyz.redtorch.common.mongo.MongoDBClient;
import xyz.redtorch.pb.CoreField.BarField;

@SpringBootTest
public class TestBarDataDao {

	BarDataDao barDao = new BarDataDaoImpl();
	
	MongoDBClient mongo;
	
	BarField[] demoData = new BarField[5];
	
	final String CONTRACT_ID = "rb2005@SHFE";
	
	@Before
	public void before() throws Exception {
		mongo = new MongoDBClient("localhost", 27017, "", "", "");
		Field field = ((BarDataDaoImpl)barDao).getClass().getDeclaredField("mongodb");
		field.setAccessible(true);
		field.set((BarDataDaoImpl)barDao, mongo);
		
		for(int i=0; i<demoData.length; i++) {
			BarField.Builder b = BarField.newBuilder();
			b.setUnifiedSymbol(CONTRACT_ID);
			LocalDateTime now = LocalDateTime.now();
			b.setActionDay(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
			b.setActionTime(now.format(DateTimeFormatter.ofPattern("HHmmssSSS")));
			b.setActionTimestamp(System.currentTimeMillis()-10000+i);
			b.setOpenPrice(ThreadLocalRandom.current().nextDouble(1000000));
			b.setHighPrice(ThreadLocalRandom.current().nextDouble(1000000));
			b.setLowPrice(ThreadLocalRandom.current().nextDouble(1000000));
			b.setClosePrice(ThreadLocalRandom.current().nextDouble(1000000));
			b.setVolume(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
			b.setVolumeDelta(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
			b.setNumTrades(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
			b.setNumTradesDelta(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
			b.setTurnover(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			b.setTurnoverDelta(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			b.setOpenInterest(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			b.setOpenInterestDelta(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			demoData[i] = b.build();
		}
		
	}
	
	@After
	public void after() {
		mongo.dropCollection("DB_BAR_DATA", CONTRACT_ID);
	}
	
	@Test
	public void test() {
		for(BarField b : demoData) {
			barDao.saveBarData(b);
		}
		
		BarField[] result = barDao.loadBarData(CONTRACT_ID, LocalDateTime.now().minusDays(1), LocalDateTime.now());
		
		assertTrue(result.length==demoData.length);
		for(int i=0; i<demoData.length; i++) {
			BarField b1 = demoData[i];
			BarField b2 = result[i];
			assertEquals(b1.getActionDay(), b2.getActionDay());
			assertEquals(b1.getActionTime(), b2.getActionTime());
			assertEquals(b1.getActionTimestamp(), b2.getActionTimestamp());
			assertTrue(Math.abs(b1.getHighPrice() - b2.getHighPrice())<0.00001);
			assertTrue(Math.abs(b1.getOpenPrice() - b2.getOpenPrice())<0.00001);
			assertTrue(Math.abs(b1.getClosePrice() - b2.getClosePrice())<0.00001);
			assertTrue(Math.abs(b1.getLowPrice() - b2.getLowPrice())<0.00001);
			assertEquals(b1.getNumTrades(), b2.getNumTrades());
			assertEquals(b1.getNumTradesDelta(), b2.getNumTradesDelta());
			assertEquals(b1.getTradingDay(), b2.getTradingDay());
			assertEquals(b1.getVolume(), b2.getVolume());
			assertEquals(b1.getVolumeDelta(), b2.getVolumeDelta());
			assertTrue(Math.abs(b1.getOpenInterest()-b2.getOpenInterest())<0.00001);
			assertTrue(Math.abs(b1.getOpenInterestDelta()-b2.getOpenInterestDelta())<0.00001);
			assertTrue(Math.abs(b1.getTurnover()-b2.getTurnover())<0.00001);
			assertTrue(Math.abs(b1.getTurnoverDelta()-b2.getTurnoverDelta())<0.00001);
		}
	} 

}
