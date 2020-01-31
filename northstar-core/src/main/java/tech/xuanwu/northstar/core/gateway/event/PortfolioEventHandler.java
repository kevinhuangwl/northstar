package tech.xuanwu.northstar.core.gateway.event;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.core.msg.MessageEngine;
import tech.xuanwu.northstar.core.util.ContractMap;
import tech.xuanwu.northstar.service.FastEventService;
import tech.xuanwu.northstar.service.FastEventService.FastEvent;
import tech.xuanwu.northstar.service.FastEventService.FastEventDynamicHandlerAbstract;
import tech.xuanwu.northstar.service.FastEventService.FastEventType;
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
	FastEventService fes;
	
	@Autowired
	MessageEngine msgEngine;
	
	@Autowired
	ContractMap contractMap;
	
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
			contractMap.registerContract(c);
			break;
		default:
			log.warn("遇到未知事件类型：{}", event.getFastEventType());
		}
	}
}
