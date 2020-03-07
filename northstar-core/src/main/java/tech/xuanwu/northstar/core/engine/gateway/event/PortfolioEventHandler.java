package tech.xuanwu.northstar.core.engine.gateway.event;

import java.util.EventObject;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.RuntimeEvent;
import tech.xuanwu.northstar.core.engine.SocketIOMessageEngine;
import tech.xuanwu.northstar.core.persistence.repo.ContractRepo;
import tech.xuanwu.northstar.core.util.FutureDictionary;
import tech.xuanwu.northstar.engine.FastEventEngine;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.entity.ContractInfo;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEvent;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEventDynamicHandlerAbstract;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEventType;
import xyz.redtorch.pb.CoreField.AccountField;
import xyz.redtorch.pb.CoreField.ContractField;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.PositionField;
import xyz.redtorch.pb.CoreField.TradeField;

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
	RuntimeEngine rtEngine;
	
	@Autowired
	ContractRepo contractRepo;
	
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
			contractRepo.insertIfAbsent(ContractInfo.convertFrom(c));
			break;
		default:
			log.warn("遇到未知事件类型：{}", event.getFastEventType());
		}
	}
}
