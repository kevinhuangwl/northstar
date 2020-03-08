package tech.xuanwu.northstar.core.engine.gateway.event;

import java.util.EventObject;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.NoticeCode;
import tech.xuanwu.northstar.engine.FastEventEngine;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEvent;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEventDynamicHandlerAbstract;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEventType;
import tech.xuanwu.northstar.entity.NoticeInfo;
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
			String noticeInfoStr = notice.getContent();
			NoticeInfo noticeInfo = new Gson().fromJson(noticeInfoStr, NoticeInfo.class);
			
			log.info(noticeInfo.getMessage());
			rtEngine.emitEvent(NoticeCode.EVENT_MAP.get(noticeInfo.getEvent()), new EventObject(noticeInfo.getData()));
		}
	}

	

}
