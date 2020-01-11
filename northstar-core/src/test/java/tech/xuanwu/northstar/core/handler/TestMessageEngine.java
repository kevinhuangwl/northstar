package tech.xuanwu.northstar.core.handler;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import tech.xuanwu.northstar.engine.MessageEngine;
import tech.xuanwu.northstar.gateway.GatewayApi;
import xyz.redtorch.gateway.ctp.x64v6v3v15v.CtpGatewayImpl;
import xyz.redtorch.pb.CoreField.ContractField;
import xyz.redtorch.pb.CoreField.TickField;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestMessageEngine{
	
	ConcurrentLinkedQueue<TickField> tq = new ConcurrentLinkedQueue<TickField>();

	@Resource(name="ctpGateway")
	GatewayApi ctpGateway;
	
	
	
	@Test
	public void test() throws InterruptedException {

		CtpGatewayImpl ctp = (CtpGatewayImpl)ctpGateway;
		assertTrue(ctp.contractMap.size()>0);
		System.out.println(ctp.contractMap.size());
		
		ContractField.Builder cBuilder = ContractField.newBuilder();
		cBuilder.setSymbol("rb2005");
		ctpGateway.subscribe(cBuilder.build());
		
		Thread.sleep(5000);
	}

}
