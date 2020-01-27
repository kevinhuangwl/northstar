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

import tech.xuanwu.northstar.core.dao.impl.TickDataDaoImpl;
import xyz.redtorch.common.mongo.MongoDBClient;
import xyz.redtorch.pb.CoreField.ContractField;
import xyz.redtorch.pb.CoreField.TickField;

@SpringBootTest
public class TestTickDataDao {

	TickDataDao tickDao = new TickDataDaoImpl();
	
	MongoDBClient mongo;
	
	TickField[] demoData = new TickField[30];
	
	final String CONTRACT_ID = "rb2005@SHFE";
	
	@Before
	public void before() throws Exception {
		mongo = new MongoDBClient("localhost", 27017, "", "", "");
		Field field = ((TickDataDaoImpl)tickDao).getClass().getDeclaredField("mongodb");
		field.setAccessible(true);
		field.set((TickDataDaoImpl)tickDao, mongo);
		
		for(int i=0; i<demoData.length; i++) {
			ContractField.Builder cb = ContractField.newBuilder();
			cb.setContractId(CONTRACT_ID);
			TickField.Builder tb = TickField.newBuilder();
			LocalDateTime now = LocalDateTime.now();
			tb.setContract(cb.build());
			tb.setTradingDay(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
			tb.setActionDay(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
			tb.setActionTime(now.format(DateTimeFormatter.ofPattern("HHmmssSSS")));
			tb.setActionTimestamp(System.currentTimeMillis()-10000+i);
			tb.setDataSourceId("rb2005@SHFE@FUTURES@CTP-SimNow724");
			tb.setOpenPrice(ThreadLocalRandom.current().nextDouble(1000000));
			tb.setHighPrice(ThreadLocalRandom.current().nextDouble(1000000));
			tb.setLowPrice(ThreadLocalRandom.current().nextDouble(1000000));
			tb.setLastPrice(ThreadLocalRandom.current().nextDouble(1000000));
			tb.setLowerLimit(ThreadLocalRandom.current().nextDouble(1000000));
			tb.setUpperLimit(ThreadLocalRandom.current().nextDouble(1000000));
			tb.setPreSettlePrice(ThreadLocalRandom.current().nextDouble(1000000));
			tb.setPreClosePrice(ThreadLocalRandom.current().nextDouble(1000000));
			tb.setPreOpenInterest(ThreadLocalRandom.current().nextDouble(1000000));
			tb.setOpenInterest(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			tb.setVolume(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
			tb.setVolumeChange(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
			tb.setTurnover(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			tb.setTurnoverChange(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			tb.setNumTrades(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
			tb.setNumTradesChange(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
			demoData[i] = tb.build();
		}
		
	}
	
	@After
	public void after() {
		mongo.dropCollection("DB_TICK_DATA", CONTRACT_ID);
	}
	
	@Test
	public void test() {
		for(TickField t : demoData) {
			tickDao.saveTickData(t);
		}
		
		TickField[] result = tickDao.loadTickData(CONTRACT_ID, LocalDateTime.now().minusDays(1), LocalDateTime.now());
		
		final double E = 0.00001;
		
		assertTrue(result.length==demoData.length);
		for(int i=0; i<demoData.length; i++) {
			TickField t1 = demoData[i];
			TickField t2 = result[i];
			assertEquals(t1.getActionDay(), t2.getActionDay());
			assertEquals(t1.getActionTime(), t2.getActionTime());
			assertEquals(t1.getActionTimestamp(), t2.getActionTimestamp());
			assertEquals(t1.getTradingDay(), t2.getTradingDay());
			assertEquals(t1.getDataSourceId(), t2.getDataSourceId());
			assertTrue(Math.abs(t1.getHighPrice() - t2.getHighPrice())<E);
			assertTrue(Math.abs(t1.getOpenPrice() - t2.getOpenPrice())<E);
			assertTrue(Math.abs(t1.getLowPrice() - t2.getLowPrice())<E);
			assertTrue(Math.abs(t1.getLastPrice() - t2.getLastPrice())<E);
			assertTrue(Math.abs(t1.getUpperLimit() - t2.getUpperLimit())<E);
			assertTrue(Math.abs(t1.getLowerLimit() - t2.getLowerLimit())<E);
			assertTrue(Math.abs(t1.getPreClosePrice() - t2.getPreClosePrice())<E);
			assertTrue(Math.abs(t1.getPreOpenInterest() - t2.getPreOpenInterest())<E);
			assertTrue(Math.abs(t1.getPreSettlePrice() - t2.getPreSettlePrice())<E);
			assertEquals(t1.getNumTrades(), t2.getNumTrades());
			assertEquals(t1.getNumTradesChange(), t2.getNumTradesChange());
			assertEquals(t1.getVolume(), t2.getVolume());
			assertEquals(t1.getVolumeChange(), t2.getVolumeChange());
			assertTrue(Math.abs(t1.getOpenInterest()-t2.getOpenInterest())<E);
			assertTrue(Math.abs(t1.getOpenInterestChange()-t2.getOpenInterestChange())<E);
			assertTrue(Math.abs(t1.getTurnover()-t2.getTurnover())<E);
			assertTrue(Math.abs(t1.getTurnoverChange()-t2.getTurnoverChange())<E);
		}
	} 
}
