package tech.xuanwu.northstar.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;
import xyz.redtorch.pb.CoreField.ContractField;

/**
 * 本单元测试需要直接调用ctp的接口数据来测试
 * @author kevinhuangwl
 *
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestContractHelper {

	@Autowired
	ContractHelper contractHelper;
	
	@Before
	public void waitASec() throws InterruptedException {
		//为确保ctp接口数据已准备好，先等待N秒
		Thread.sleep(5*1000);
	}
	
	@Test
	public void test() {
		
		ContractField c = contractHelper.getContractBySymbol("rb2005");
		assertEquals(c.getSymbol(), "rb2005");
		log.info(c.toString());
		
		Collection<ContractField> results = contractHelper.getAllMonthContracts("rb0000");
		assertTrue(results.size()==12);
		for(ContractField cf : results) {
			log.info(cf.toString());
		}
	}
}
