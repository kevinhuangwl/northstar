package tech.xuanwu.northstar.core.dao;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import xyz.redtorch.pb.CoreField.BarField;
import xyz.redtorch.pb.CoreField.ContractField;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestBarDataDao {

	@Autowired
	BarDataDao barDao;
	
	BarField[] demoData = new BarField[5];
	
	final String CONTRACT_ID = "rb2005@SHFE";
	
	@Before
	public void before() {
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
			b.setOpenPrice(1000);
			b.setHighPrice(2000);
			b.setLowPrice(500);
			b.setClosePrice(1111);
			demoData[i] = b.build();
		}
		
	}
	
	@Test
	public void test() {
		for(BarField b : demoData) {
			barDao.saveBarData(b);
		}
		
		
		BarField[] result = barDao.loadBarData(CONTRACT_ID, LocalDateTime.now().minusDays(1), LocalDateTime.now());
		
		assertTrue(result.length==demoData.length);
		for(int i=0; i<demoData.length; i++) {
			
			assertTrue(demoData[i].getActionTimestamp()==result[i].getActionTimestamp());
		}
	}

}
