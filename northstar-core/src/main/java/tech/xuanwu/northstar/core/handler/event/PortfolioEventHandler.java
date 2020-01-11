package tech.xuanwu.northstar.core.handler.event;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.core.engine.MessageEngine;
import xyz.redtorch.common.service.FastEventService;
import xyz.redtorch.common.service.FastEventService.FastEvent;
import xyz.redtorch.common.service.FastEventService.FastEventDynamicHandlerAbstract;
import xyz.redtorch.common.service.FastEventService.FastEventType;

/**
 * 投资组合相关事件处理器
 * @author kevinhuangwl
 *
 */
@Slf4j
@Component
public class PortfolioEventHandler extends FastEventDynamicHandlerAbstract implements InitializingBean{
	
	@Autowired
	FastEventService fes;
	
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
