package tech.xuanwu.northstar.core.gateway.event;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.service.FastEventService;
import tech.xuanwu.northstar.service.FastEventService.FastEvent;
import tech.xuanwu.northstar.service.FastEventService.FastEventDynamicHandlerAbstract;
import tech.xuanwu.northstar.service.FastEventService.FastEventType;

/**
 * 通知事件处理器
 * @author kevinhuangwl
 *
 */
@Slf4j
//@Component
public class NoticeEventHandler extends FastEventDynamicHandlerAbstract implements InitializingBean{
	
	@Autowired
	FastEventService fes;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		fes.addHandler(this);
		subscribeFastEventType(FastEventType.NOTICE);
	}
	
	@Override
	public void onEvent(FastEvent event, long sequence, boolean endOfBatch) throws Exception {
		// TODO Auto-generated method stub
		
	}

	

}
