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
import xyz.redtorch.common.util.CommonUtils;
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
			LocalDateTime now = LocalDateTime.now();
			b.setUnifiedSymbol(CONTRACT_ID);
			b.setGatewayId("CTP");
			b.setTradingDay(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
			b.setActionDay(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
			b.setActionTime(now.format(DateTimeFormatter.ofPattern("HHmmssSSS")));
			b.setActionTimestamp(System.currentTimeMillis()-10000+i);
			b.setOpenPrice(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			b.setHighPrice(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			b.setLowPrice(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			b.setClosePrice(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			b.setVolume(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
			b.setVolumeDelta(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
			b.setNumTrades(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
			b.setNumTradesDelta(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
			b.setTurnover(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			b.setTurnoverDelta(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			b.setOpenInterest(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			b.setOpenInterestDelta(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			b.setPreClosePrice(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			b.setPreOpenInterest(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			b.setPreSettlePrice(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
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
			b1.equals(b2);
		}
	} 

}
