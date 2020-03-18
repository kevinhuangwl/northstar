package tech.xuanwu.northstar.core.integrated;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testng.annotations.BeforeClass;

import tech.xuanwu.northstar.core.persistence.repo.ContractRepo;
import tech.xuanwu.northstar.entity.ContractInfo;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestSimulatedGateway{
	
	ContractInfo c;
	
	String demoContract = "{\n" + 
			"    \"_id\" : ObjectId(\"5e6ca177c1e5f476f01f324e\"),\n" + 
			"    \"contractId\" : \"rb2005@SHFE@FUTURES@CTP-SimNow724@Simulated\",\n" + 
			"    \"name\" : \"螺纹钢2005\",\n" + 
			"    \"fullName\" : \"螺纹钢2005\",\n" + 
			"    \"thirdPartyId\" : \"rb2005\",\n" + 
			"    \"unifiedSymbol\" : \"rb2005@SHFE@FUTURES\",\n" + 
			"    \"symbol\" : \"rb2005\",\n" + 
			"    \"exchange\" : \"SHFE\",\n" + 
			"    \"productClass\" : \"FUTURES\",\n" + 
			"    \"currency\" : \"CNY\",\n" + 
			"    \"multiplier\" : 10.0,\n" + 
			"    \"priceTick\" : 1.0,\n" + 
			"    \"longMarginRatio\" : 0.1,\n" + 
			"    \"shortMarginRatio\" : 0.1,\n" + 
			"    \"maxMarginSideAlgorithm\" : true,\n" + 
			"    \"underlyingSymbol\" : \"\",\n" + 
			"    \"strikePrice\" : 0.0,\n" + 
			"    \"optionsType\" : \"O_Unknown\",\n" + 
			"    \"underlyingMultiplier\" : 0.0,\n" + 
			"    \"lastTradeDateOrContractMonth\" : \"20200515\",\n" + 
			"    \"maxMarketOrderVolume\" : 30,\n" + 
			"    \"minMarketOrderVolume\" : 1,\n" + 
			"    \"maxLimitOrderVolume\" : 500,\n" + 
			"    \"minLimitOrderVolume\" : 1,\n" + 
			"    \"combinationType\" : \"COMBT_Unknown\",\n" + 
			"    \"gatewayId\" : \"CTP-SimNow724@Simulated\",\n" + 
			"    \"isSubscribed\" : true\n" + 
			"}";
	
	String tickProto = "";
	
	@BeforeClass
	public void beforeClass() {
		
		System.out.println("beforeClass");
	}

	@Test
	public void f() {
		System.out.println("testing");
	}
}
