package tech.xuanwu.northstar.core.engine.gateway.event;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.SimulatedGatewayImpl;
import tech.xuanwu.northstar.engine.FastEventEngine;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEvent;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEventDynamicHandlerAbstract;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEventType;
import tech.xuanwu.northstar.gateway.GatewayApi;
import xyz.redtorch.pb.CoreField.TickField;

@Slf4j
@Component
@ConditionalOnExpression("!${ctp.realTrader}")
public class TickEventLoopBackHandler extends FastEventDynamicHandlerAbstract implements InitializingBean{

	@Autowired
	FastEventEngine fes;
	
	@Setter
	GatewayApi simulatedGateway;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("加载行情回输处理器");
		
		fes.addHandler(this);
		
		subscribeFastEventType(FastEventType.TICK);
	}
	
	@Override
	public void onEvent(FastEvent fastEvent, long sequence, boolean endOfBatch) throws Exception {
		if (FastEventType.TICK.equals(fastEvent.getFastEventType())) {
			try {
				TickField tick = (TickField) fastEvent.getObj();
				
				if(simulatedGateway!=null && simulatedGateway instanceof SimulatedGatewayImpl) {
					simulatedGateway.emitTick(tick);
				}
			} catch (Exception e) {
				log.error("Tick事件发生异常", e);
			}
		}
	}
	
}
