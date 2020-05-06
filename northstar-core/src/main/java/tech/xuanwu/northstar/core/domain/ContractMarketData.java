package tech.xuanwu.northstar.core.domain;

import tech.xuanwu.northstar.entity.DayBarInfo;
import tech.xuanwu.northstar.entity.MinTickInfo;
import tech.xuanwu.northstar.persistence.dao.BarDataDao;
import tech.xuanwu.northstar.persistence.dao.TickDataDao;
import xyz.redtorch.common.util.bar.BarGenerator;
import xyz.redtorch.pb.CoreField.BarField;
import xyz.redtorch.pb.CoreField.TickField;

/**
 * 合约行情对象
 * 一个实例代表一个合约的行情数据
 * @author kevinhuangwl
 *
 */
public class ContractMarketData {
	
	private TickDataDao tickDao;
	
	private BarDataDao barDao;
	
	private volatile MinTickInfo currentMinTick;
	
	private volatile DayBarInfo currentDayBar;
	
	public ContractMarketData(TickDataDao tickDao, BarDataDao barDao) {
		this.tickDao = tickDao;
		this.barDao = barDao;
	}
	
	private BarGenerator barGen = new BarGenerator((bar)->{
		saveBar(bar);
	});
	
	public void updateTick(TickField tick) {
		saveTick(tick);
		
		barGen.updateTick(tick);
	}

	public void saveTick(TickField tick) {
		if(currentMinTick == null) {
			currentMinTick = tickDao.loadTickData(tick.getUnifiedSymbol(), tick.getTradingDay(), tick.getActionTime().substring(0, 4));
			currentMinTick = currentMinTick == null ? MinTickInfo.convertFrom(tick) : currentMinTick;
		}
		if(!tick.getActionTime().startsWith(currentMinTick.getActionTimeMin())) {
			//每分钟保存一次
			tickDao.saveTickData(currentMinTick);
			currentMinTick = MinTickInfo.convertFrom(tick);
		}
		currentMinTick.addTick(tick);
	}
	
	public void saveBar(BarField bar) {
		if(currentDayBar == null) {
			currentDayBar = barDao.loadBarData(bar.getUnifiedSymbol(), bar.getTradingDay());
			currentDayBar = currentDayBar == null ? DayBarInfo.convertFrom(bar) : currentDayBar;
		}
		//每分钟保存一次
		barDao.saveBarData(currentDayBar);
		if(!bar.getTradingDay().equals(currentDayBar.getTradingDay())) {
			currentDayBar = DayBarInfo.convertFrom(bar);
		}
		currentDayBar.addBar(bar);
	}
}
