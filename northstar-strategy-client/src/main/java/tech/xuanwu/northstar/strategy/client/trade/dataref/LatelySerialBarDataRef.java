package tech.xuanwu.northstar.strategy.client.trade.dataref;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import tech.xuanwu.northstar.persistence.dao.BarDataDao;
import tech.xuanwu.northstar.strategy.DataRef;
import tech.xuanwu.northstar.strategy.Indicator;
import xyz.redtorch.pb.CoreField.BarField;

/**
 * 连续K线行情回溯器
 * @author kevinhuangwl
 *
 */
public class LatelySerialBarDataRef implements DataRef<BarField>{
	
	private List<Indicator> subIndicators = new CopyOnWriteArrayList<>();
	
	//最大回溯长度
	private int maxRefLen;
	
	private ArrayBlockingQueue<BarField> barQ;
	
	private BarDataDao barDao;
	
	private String gatewayId;
	
	private String contractSymbol;
	
	public LatelySerialBarDataRef(String gatewayId, String contractSymbol, int maxRefLen, BarDataDao barDao) {
		this.gatewayId = gatewayId;
		this.contractSymbol = contractSymbol;
		this.barDao = barDao;
		this.maxRefLen = maxRefLen;
		barQ = new ArrayBlockingQueue<>(maxRefLen);
	}

	@Override
	public void load(int numOfRef) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateData(BarField bar) {
		if(barQ.size() == maxRefLen) {
			barQ.poll();
		}
		barQ.offer(bar);
		for(Indicator i : subIndicators) {
			i.update(bar);
		}
	}

	@Override
	public List<BarField> getDataRef() {
		List<BarField> resultList = new ArrayList<>(maxRefLen);
		resultList.addAll(barQ);
		return resultList;
	}

	@Override
	public void addIndicator(Indicator indicator) {
		subIndicators.add(indicator);
	}

}
