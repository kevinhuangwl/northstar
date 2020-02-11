package tech.xuanwu.northstar.core.engine.gateway.event;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.core.engine.SocketIOMessageEngine;
import tech.xuanwu.northstar.core.util.FutureDictionary;
import tech.xuanwu.northstar.engine.FastEventEngine;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEvent;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEventDynamicHandlerAbstract;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEventType;
import xyz.redtorch.pb.CoreField.ContractField;

/**
 * 投资组合相关事件处理器
 * @author kevinhuangwl
 *
 */
@Slf4j
@Component
public class PortfolioEventHandler extends FastEventDynamicHandlerAbstract implements InitializingBean{
	
	@Autowired
	FastEventEngine fes;
	
	@Autowired
	SocketIOMessageEngine msgEngine;
	
	@Autowired
	FutureDictionary futureDict;
	
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
		if(!subscribedFastEventTypeSet.contains(event.getFastEventType())) {
			return;
		}
		
		//发生频率越高的事件排得越前
		switch(event.getFastEventType()) {
		case ACCOUNT:
			
			break;
		case POSITION:
			
			break;
		case ORDER:
			
			break;
		case TRADE:
			
			break;
		case CONTRACT:
			ContractField c = (ContractField) event.getObj();
			futureDict.add(c);
			break;
		default:
			log.warn("遇到未知事件类型：{}", event.getFastEventType());
		}
	}
}
