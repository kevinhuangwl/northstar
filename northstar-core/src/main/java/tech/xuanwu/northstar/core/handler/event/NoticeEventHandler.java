package tech.xuanwu.northstar.core.handler.event;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.engine.FastEventEngine;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEvent;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEventDynamicHandlerAbstract;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEventType;

/**
 * 通知事件处理器
 * @author kevinhuangwl
 *
 */
@Slf4j
@Component
public class NoticeEventHandler extends FastEventDynamicHandlerAbstract implements InitializingBean{
	
	@Autowired
	FastEventEngine fes;
	
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
