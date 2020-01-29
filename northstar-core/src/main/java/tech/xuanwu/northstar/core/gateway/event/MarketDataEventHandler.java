package tech.xuanwu.northstar.core.gateway.event;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.core.dao.BarDataDao;
import tech.xuanwu.northstar.core.dao.TickDataDao;
import tech.xuanwu.northstar.engine.FastEventEngine;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEvent;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEventDynamicHandlerAbstract;
import tech.xuanwu.northstar.engine.FastEventEngine.FastEventType;
import xyz.redtorch.common.util.BarGenerator;
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
	TickDataDao tickDao;
	
	@Autowired
	BarDataDao barDao;
	
	ConcurrentHashMap<String, BarGenerator> barGeneratorMap = new ConcurrentHashMap<String, BarGenerator>(100);
	
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
				tickDao.saveTickData(tick);
				
				String contractId = tick.getContract().getContractId();
				if(!barGeneratorMap.containsKey(contractId)) {
					barGeneratorMap.put(contractId, new BarGenerator(barCallback)); 
				}
				
				barGeneratorMap.get(contractId).updateTick(tick);
				
			} catch (Exception e) {
				log.error("行情事件发生异常", e);
			}
		}
	}

	BarGenerator.CommonBarCallBack barCallback = (barField)->{
		barDao.saveBarData(barField);
	};
	

}
