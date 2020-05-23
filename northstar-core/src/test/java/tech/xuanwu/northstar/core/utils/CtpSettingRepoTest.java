package tech.xuanwu.northstar.core.utils;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.gson.Gson;

import tech.xuanwu.northstar.core.persistence.repo.CtpSettingRepo;
import tech.xuanwu.northstar.entity.CtpSettingInfo;
import tech.xuanwu.northstar.entity.CtpSettingInfo.ConnectionType;
import tech.xuanwu.northstar.entity.CtpSettingInfo.MarketType;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CtpSettingRepoTest {

	@Autowired
	CtpSettingRepo repo;
	
	@Test
	public void testUpsert() {
		CtpSettingInfo info1 = new CtpSettingInfo();
		info1.setGatewayId("Simnow724");
		info1.setGatewayName("Simnow724交易账户");
		info1.setMdHost("180.168.146.187");
		info1.setMdPort("10131");
		info1.setTdHost("180.168.146.187");
		info1.setTdPort("10130");
		info1.setAppId("client_northstar_v1.0");
		info1.setUserProductInfo("client_northstar_v1.0");
		info1.setAuthCode("0000000000000000");
		info1.setBrokerId("9999");
		info1.setUserId("094020");
		info1.setPassword("ikevin1984");
		info1.setConnectionType(ConnectionType.ACCOUNT);
		info1.setMarketType(MarketType.SIMULATE);
		
		repo.save(info1);
	}
	
	@Test
	public void testFind() {
		List<CtpSettingInfo> list1 = repo.findByConnectionTypeAndMarketType(ConnectionType.ACCOUNT, MarketType.REAL);
		List<CtpSettingInfo> list2 = repo.findByConnectionTypeAndMarketType(ConnectionType.ACCOUNT, MarketType.SIMULATE);
		List<CtpSettingInfo> list3 = repo.findByConnectionTypeAndMarketType(ConnectionType.MARKET, MarketType.REAL);
		List<CtpSettingInfo> list4 = repo.findByConnectionTypeAndMarketType(ConnectionType.MARKET, MarketType.SIMULATE);
		
		Gson gson = new Gson();
		System.out.println(gson.toJson(list1));
		System.out.println(gson.toJson(list2));
		System.out.println(gson.toJson(list3));
		System.out.println(gson.toJson(list4));
	}
	
}
