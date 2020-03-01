package tech.xuanwu.northstar.core.engine.gateway.event;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.NoticeCode;
import tech.xuanwu.northstar.engine.FastEventEngine;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEvent;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEventDynamicHandlerAbstract;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEventType;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import xyz.redtorch.pb.CoreEnum.CommonStatusEnum;
import xyz.redtorch.pb.CoreField.NoticeField;

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
	
	@Autowired
	RuntimeEngine rtEngine;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		fes.addHandler(this);
		subscribeFastEventType(FastEventType.NOTICE);
	}
	
	@Override
	public void onEvent(FastEvent event, long sequence, boolean endOfBatch) throws Exception {
		if(event.getFastEventType() != FastEventType.NOTICE) {
			return;
		}
		
		NoticeField notice = (NoticeField) event.getObj();
		if(notice.getStatus() == CommonStatusEnum.COMS_SUCCESS) {
			String code = notice.getContent().substring(0, 3);
			rtEngine.emitEvent(NoticeCode.EVENT_MAP.get(code), null);
		}
	}

	

}
