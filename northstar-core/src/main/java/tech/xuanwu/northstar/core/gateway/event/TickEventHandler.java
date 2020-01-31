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
import xyz.redtorch.pb.CoreField.TickField;

/**
 * Tick事件处理器，专门用于推送行情数据
 * @author kevinhuangwl
 *
 */
@Slf4j
@Component
public class TickEventHandler extends FastEventDynamicHandlerAbstract implements InitializingBean{

	@Autowired
	FastEventEngine fes;
	
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
				log.error("Tick事件发生异常", e);
			}
		}
	}

	

}
