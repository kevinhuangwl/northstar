package tech.xuanwu.northstar.core.engine.gateway.event;

import java.util.EventObject;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.SimulatedGatewayImpl;
import tech.xuanwu.northstar.constant.NoticeCode;
import tech.xuanwu.northstar.constant.RuntimeEvent;
import tech.xuanwu.northstar.core.engine.SocketIOMessageEngine;
import tech.xuanwu.northstar.core.persistence.repo.ContractRepo;
import tech.xuanwu.northstar.engine.FastEventEngine;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEvent;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEventDynamicHandlerAbstract;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEventType;
import tech.xuanwu.northstar.engine.IndexEngine;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.entity.ContractInfo;
import tech.xuanwu.northstar.entity.NoticeInfo;
import tech.xuanwu.northstar.exception.NoSuchEventHandlerException;
import tech.xuanwu.northstar.gateway.GatewayApi;
import xyz.redtorch.pb.CoreEnum.CommonStatusEnum;
import xyz.redtorch.pb.CoreField.AccountField;
import xyz.redtorch.pb.CoreField.ContractField;
import xyz.redtorch.pb.CoreField.NoticeField;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.PositionField;
import xyz.redtorch.pb.CoreField.TickField;
import xyz.redtorch.pb.CoreField.TradeField;

/**
 * 网关事件处理器。主要处理事件分支
 * @author kevinhuangwl
 *
 */
@Slf4j
@Component
public class GatewayEventHandler extends FastEventDynamicHandlerAbstract implements InitializingBean{
	
	@Autowired
	FastEventEngine fes;
	
	@Autowired
	SocketIOMessageEngine msgEngine;
	
	@Autowired
	RuntimeEngine rtEngine;
	
	@Autowired
	ContractRepo contractRepo;
	
	@Autowired
	IndexEngine idxEngine;
	
	@Autowired(required = false)
	@Qualifier("simulatedGateway")
	GatewayApi simulatedGateway;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		fes.addHandler(this);
		
		subscribeFastEventType(FastEventType.TICK);
		subscribeFastEventType(FastEventType.ACCOUNT);
		subscribeFastEventType(FastEventType.CONTRACT);
		subscribeFastEventType(FastEventType.ORDER);
		subscribeFastEventType(FastEventType.POSITION);
		subscribeFastEventType(FastEventType.TRADE);
		subscribeFastEventType(FastEventType.NOTICE);
		
		log.info("事件处理器注册完成");
	}

	@Override
	public void onEvent(FastEvent event, long sequence, boolean endOfBatch) throws Exception {
		if(!subscribedFastEventTypeSet.contains(event.getFastEventType())) {
			return;
		}
		
		//发生频率越高的事件排得越前
		switch(event.getFastEventType()) {
		case TICK:
			try {
				TickField tick = (TickField) event.getObj();
				onTick(tick);
			} catch (Exception e) {
				log.error("行情事件发生异常", e);
			}
			break;
		case ACCOUNT:
			AccountField account = (AccountField) event.getObj();
			rtEngine.emitEvent(RuntimeEvent.FEEDBACK_ACCOUNT, new EventObject(account));
			break;
		case POSITION:
			PositionField position = (PositionField) event.getObj();
			rtEngine.emitEvent(RuntimeEvent.FEEDBACK_POSITION, new EventObject(position));
			break;
		case ORDER:
			OrderField order = (OrderField) event.getObj();
			rtEngine.emitEvent(RuntimeEvent.FEEDBACK_ORDER, new EventObject(order));
			break;
		case TRADE:
			TradeField trade = (TradeField) event.getObj();
			rtEngine.emitEvent(RuntimeEvent.FEEDBACK_TRADE, new EventObject(trade));
			break;
		case CONTRACT:
			ContractField c = (ContractField) event.getObj();
			ContractInfo contract = ContractInfo.convertFrom(c);
			contractRepo.insertIfAbsent(contract);
			break;
		case NOTICE:
			NoticeField notice = (NoticeField) event.getObj();
			if(notice.getStatus() == CommonStatusEnum.COMS_SUCCESS) {
				String noticeInfoStr = notice.getContent();
				NoticeInfo noticeInfo = new Gson().fromJson(noticeInfoStr, NoticeInfo.class);
				
				log.info(noticeInfo.getMessage());
				rtEngine.emitEvent(NoticeCode.EVENT_MAP.get(noticeInfo.getEvent()), new EventObject(noticeInfo.getData()));
			}
			break;
		default:
			log.warn("遇到未知事件类型：{}", event.getFastEventType());
		}
	}
	
	//处理TICK
	private void onTick(TickField tick) throws NoSuchEventHandlerException {
		//更新指数合约
		idxEngine.updateTick(tick);
		
		//回输模拟网关
		if(simulatedGateway!=null && simulatedGateway instanceof SimulatedGatewayImpl) {
			simulatedGateway.emitTick(tick);
		}
		
		//后续处理
		EventObject e = new EventObject(tick);
		rtEngine.emitEvent(RuntimeEvent.TICK_UPDATE, e);
	}
}
