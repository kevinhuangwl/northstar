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
import xyz.redtorch.pb.CoreField.ContractField;

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
			ContractField.Builder cb = ContractField.newBuilder();
			cb.setContractId(CONTRACT_ID);
			BarField.Builder b = BarField.newBuilder();
			b.setContract(cb.build());
			LocalDateTime now = LocalDateTime.now();
			b.setActionDay(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
			b.setActionTime(now.format(DateTimeFormatter.ofPattern("HHmmssSSS")));
			b.setActionTimestamp(System.currentTimeMillis()-10000+i);
			b.setDataSourceId("rb2005@SHFE@FUTURES@CTP-SimNow724");
			b.setOpenPrice(ThreadLocalRandom.current().nextDouble(1000000));
			b.setHighPrice(ThreadLocalRandom.current().nextDouble(1000000));
			b.setLowPrice(ThreadLocalRandom.current().nextDouble(1000000));
			b.setClosePrice(ThreadLocalRandom.current().nextDouble(1000000));
			b.setVolume(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
			b.setVolumeChange(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
			b.setNumTrades(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
			b.setNumTradesChange(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
			b.setTurnover(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			b.setTurnoverChange(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			b.setOpenInterest(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			b.setOpenInterestChange(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
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
			assertEquals(b1.getDataSourceId(), b2.getDataSourceId());
			assertTrue(Math.abs(b1.getHighPrice() - b2.getHighPrice())<0.00001);
			assertTrue(Math.abs(b1.getOpenPrice() - b2.getOpenPrice())<0.00001);
			assertTrue(Math.abs(b1.getClosePrice() - b2.getClosePrice())<0.00001);
			assertTrue(Math.abs(b1.getLowPrice() - b2.getLowPrice())<0.00001);
			assertEquals(b1.getNumTrades(), b2.getNumTrades());
			assertEquals(b1.getNumTradesChange(), b2.getNumTradesChange());
			assertEquals(b1.getTradingDay(), b2.getTradingDay());
			assertEquals(b1.getVolume(), b2.getVolume());
			assertEquals(b1.getVolumeChange(), b2.getVolumeChange());
			assertTrue(Math.abs(b1.getOpenInterest()-b2.getOpenInterest())<0.00001);
			assertTrue(Math.abs(b1.getOpenInterestChange()-b2.getOpenInterestChange())<0.00001);
			assertTrue(Math.abs(b1.getTurnover()-b2.getTurnover())<0.00001);
			assertTrue(Math.abs(b1.getTurnoverChange()-b2.getTurnoverChange())<0.00001);
		}
	} 

}
