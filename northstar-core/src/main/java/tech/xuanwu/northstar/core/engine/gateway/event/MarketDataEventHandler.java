package tech.xuanwu.northstar.core.engine.gateway.event;

import java.util.EventObject;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.RuntimeEvent;
import tech.xuanwu.northstar.engine.FastEventEngine;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEvent;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEventDynamicHandlerAbstract;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEventType;
import tech.xuanwu.northstar.engine.IndexEngine;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import xyz.redtorch.pb.CoreField.TickField;

/**
 * 行情事件处理器，专门用于记录行情数据
 * @author kevinhuangwl
 *
 */
@Slf4j
@Component
public class MarketDataEventHandler extends FastEventDynamicHandlerAbstract implements InitializingBean{
	
	@Autowired
	FastEventEngine fes;
	
	@Autowired
	RuntimeEngine rtEngine;
	
	@Autowired
	IndexEngine idxEngine;
	
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
				
				idxEngine.updateTick(tick);
				
				EventObject e = new EventObject(tick);
				rtEngine.emitEvent(RuntimeEvent.TICK_UPDATE, e);
			} catch (Exception e) {
				log.error("行情事件发生异常", e);
			}
		}
	}
}
