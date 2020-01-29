package tech.xuanwu.northstar.core.gateway.event;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.core.msg.MessageEngine;
import tech.xuanwu.northstar.engine.FastEventEngine;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEvent;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEventDynamicHandlerAbstract;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEventType;

/**
 * 投资组合相关事件处理器
 * @author kevinhuangwl
 *
 */
@Slf4j
//@Component
public class PortfolioEventHandler extends FastEventDynamicHandlerAbstract implements InitializingBean{
	
	@Autowired
	FastEventEngine fes;
	
	@Autowired
	MessageEngine msgEngine;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		fes.addHandler(this);
		
		subscribeFastEventType(FastEventType.ACCOUNT);
		subscribeFastEventType(FastEventType.CONTRACT);
		subscribeFastEventType(FastEventType.ORDER);
		subscribeFastEventType(FastEventType.POSITION);
		subscribeFastEventType(FastEventType.TRADE);
		
	}

	@Override
	public void onEvent(FastEvent event, long sequence, boolean endOfBatch) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
