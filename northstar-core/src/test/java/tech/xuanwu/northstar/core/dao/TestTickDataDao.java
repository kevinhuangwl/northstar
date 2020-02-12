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
import xyz.redtorch.common.util.CommonUtils;
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
			TickField.Builder tb = TickField.newBuilder();
			LocalDateTime now = LocalDateTime.now();
			tb.setUnifiedSymbol(CONTRACT_ID);
			tb.setGatewayId("CTP");			
			tb.setTradingDay(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
			tb.setActionDay(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
			tb.setActionTime(now.format(DateTimeFormatter.ofPattern("HHmmssSSS")));
			tb.setActionTimestamp(System.currentTimeMillis()-10000+i);
			tb.setStatus(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
			tb.setLastPrice(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			tb.setAvgPrice(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			tb.setTotalAskVol(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
			tb.setTotalBidVol(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
			tb.setWeightedAvgAskPrice(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			tb.setWeightedAvgBidPrice(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			tb.setIopv(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			tb.setYieldToMaturity(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			tb.setVolume(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
			tb.setVolumeDelta(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
			tb.setTurnover(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			tb.setTurnoverDelta(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			tb.setNumTrades(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
			tb.setNumTradesDelta(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
			tb.setOpenInterest(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			tb.setOpenInterestDelta(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			tb.setPreSettlePrice(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			tb.setSettlePrice(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			tb.setPreClosePrice(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			tb.setPreOpenInterest(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			tb.setOpenPrice(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			tb.setHighPrice(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			tb.setLowPrice(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			tb.setLowerLimit(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
			tb.setUpperLimit(ThreadLocalRandom.current().nextDouble(Double.MAX_VALUE));
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
		
		assertEquals(result.length, demoData.length);
		for(int i=0; i<demoData.length; i++) {
			TickField t1 = demoData[i];
			TickField t2 = result[i];
			assertEquals(t1.getUnifiedSymbol(), t2.getUnifiedSymbol());
			assertEquals(t1.getGatewayId(), t2.getGatewayId());
			assertEquals(t1.getTradingDay(), t2.getTradingDay());
			assertEquals(t1.getActionDay(), t2.getActionDay());
			assertEquals(t1.getActionTime(), t2.getActionTime());
			assertEquals(t1.getActionTimestamp(), t2.getActionTimestamp());
			assertEquals(t1.getStatus(), t2.getStatus());
			assertTrue(CommonUtils.isEquals(t1.getLastPrice(), t2.getLastPrice()));
			assertTrue(CommonUtils.isEquals(t1.getAvgPrice(), t2.getAvgPrice()));
			assertTrue(CommonUtils.isEquals(t1.getTotalAskVol(), t2.getTotalAskVol()));
			assertTrue(CommonUtils.isEquals(t1.getTotalBidVol(), t2.getTotalBidVol()));
			assertTrue(CommonUtils.isEquals(t1.getWeightedAvgAskPrice(), t2.getWeightedAvgAskPrice()));
			assertTrue(CommonUtils.isEquals(t1.getWeightedAvgBidPrice(), t2.getWeightedAvgBidPrice()));
			assertTrue(CommonUtils.isEquals(t1.getIopv(), t2.getIopv()));
			assertTrue(CommonUtils.isEquals(t1.getYieldToMaturity(), t2.getYieldToMaturity()));
			assertTrue(CommonUtils.isEquals(t1.getVolume(), t2.getVolume()));
			assertTrue(CommonUtils.isEquals(t1.getVolumeDelta(), t2.getVolumeDelta()));
			assertTrue(CommonUtils.isEquals(t1.getTurnover(), t2.getTurnover()));
			assertTrue(CommonUtils.isEquals(t1.getTurnoverDelta(), t2.getTurnoverDelta()));
			assertTrue(CommonUtils.isEquals(t1.getNumTrades(), t2.getNumTrades()));
			assertTrue(CommonUtils.isEquals(t1.getNumTradesDelta(), t2.getNumTradesDelta()));
			assertTrue(CommonUtils.isEquals(t1.getOpenInterest(), t2.getOpenInterest()));
			assertTrue(CommonUtils.isEquals(t1.getOpenInterestDelta(), t2.getOpenInterestDelta()));
			assertTrue(CommonUtils.isEquals(t1.getPreClosePrice(), t2.getPreClosePrice()));
			assertTrue(CommonUtils.isEquals(t1.getPreOpenInterest(), t2.getPreOpenInterest()));
			assertTrue(CommonUtils.isEquals(t1.getPreSettlePrice(), t2.getPreSettlePrice()));
			assertTrue(CommonUtils.isEquals(t1.getSettlePrice(), t2.getSettlePrice()));
			assertTrue(CommonUtils.isEquals(t1.getOpenPrice(), t2.getOpenPrice()));
			assertTrue(CommonUtils.isEquals(t1.getHighPrice(), t2.getHighPrice()));
			assertTrue(CommonUtils.isEquals(t1.getLowPrice(), t2.getLowPrice()));
			assertTrue(CommonUtils.isEquals(t1.getUpperLimit(), t2.getUpperLimit()));
			assertTrue(CommonUtils.isEquals(t1.getLowerLimit(), t2.getLowerLimit()));
		}
	} 
}
