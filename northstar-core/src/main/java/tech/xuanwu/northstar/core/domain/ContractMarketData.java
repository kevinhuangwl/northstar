package tech.xuanwu.northstar.core.domain;

import org.springframework.beans.factory.annotation.Autowired;

import tech.xuanwu.northstar.core.persistence.dao.BarDataDao;
import tech.xuanwu.northstar.core.persistence.dao.TickDataDao;
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
	
	@Autowired
	private TickDataDao tickDao;
	
	@Autowired
	private BarDataDao barDao;
	
	private BarGenerator barGen = new BarGenerator((bar)->{
		saveBar(bar);
	});
	
	public void updateTick(TickField tick) {
		saveTick(tick);
		
		barGen.updateTick(tick);
	}

	public void saveTick(TickField tick) {
		tickDao.saveTickData(tick);
	}
	
	public void saveBar(BarField bar) {
		barDao.saveBarData(bar);
	}
}
