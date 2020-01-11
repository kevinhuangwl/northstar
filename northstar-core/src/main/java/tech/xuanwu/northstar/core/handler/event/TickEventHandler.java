package tech.xuanwu.northstar.core.handler.event;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.core.engine.MessageEngine;
import xyz.redtorch.common.service.FastEventService;
import xyz.redtorch.common.service.FastEventService.FastEventType;
import xyz.redtorch.pb.CoreField.TickField;
import xyz.redtorch.common.service.FastEventService.FastEvent;
import xyz.redtorch.common.service.FastEventService.FastEventDynamicHandlerAbstract;

/**
 * Tick事件处理器
 * @author kevinhuangwl
 *
 */
@Slf4j
@Component
public class TickEventHandler extends FastEventDynamicHandlerAbstract implements InitializingBean{

	@Autowired
	FastEventService fes;
	
	@Autowired
	MessageEngine msgEngine;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		fes.addHandler(this);
		
		subscribeFastEventType(FastEventType.TICK);
	}
	
	@Override
	public void onEvent(FastEvent fastEvent, long sequence, boolean endOfBatch) throws Exception {
		if (FastEventType.TICK.equals(fastEvent.getFastEventType())) {
			try {
				TickField tick = (TickField) fastEvent.getObj();
				msgEngine.emitTick(tick);
			} catch (Exception e) {
				log.error("onTick发生异常", e);
			}
		}
	}

	

}
