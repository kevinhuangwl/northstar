package tech.xuanwu.northstar.entity;

import java.util.concurrent.ConcurrentLinkedQueue;

import xyz.redtorch.pb.CoreField.TickField;

public class TickFactory {

	static final ConcurrentLinkedQueue<Tick> objPool = new ConcurrentLinkedQueue<Tick>();
	
	public static Tick getTickAs(TickField tick) {
		Tick t = objPool.poll();
		if(t == null) {
			t = new Tick();
		}
		
		t.actionDay = tick.getActionDay();
		t.tradingDay = tick.getTradingDay();
		t.actionTime = tick.getActionTime();
		t.dataSourceId = tick.getDataSourceId();
		t.actionTimestamp = tick.getActionTimestamp();
		t.askPrice = tick.getAskPrice(0);
		t.askVolume = tick.getAskVolume(0);
		t.bidPrice = tick.getBidPrice(0);
		t.bidVolume = tick.getBidVolume(0);
		t.avgPrice = tick.getAvgPrice();
		t.openPrice = tick.getOpenPrice();
		t.highPrice = tick.getHighPrice();
		t.lastPrice = tick.getLastPrice();
		t.lowPrice = tick.getLowPrice();
		t.lowerLimit = tick.getLowerLimit();
		t.upperLimit = tick.getUpperLimit();
		t.settlePrice = tick.getSettlePrice();
		t.preSettlePrice = tick.getPreSettlePrice();
		t.preClosePrice = tick.getPreClosePrice();
		t.preOpenInterest = tick.getPreOpenInterest();
		t.openInterest = tick.getOpenInterest();
		t.openInterestChange = tick.getOpenInterestChange();
		t.turnover = tick.getTurnover();
		t.volume = tick.getVolume();
		t.volumeChange = tick.getVolumeChange();
		
		return t;
	}
	
	public static void releaseTick(Tick t) {
		t.actionDay = "";
		t.tradingDay = "";
		t.actionTime = "";
		t.dataSourceId = "";
		t.actionTimestamp = 0L;
		t.askPrice = 0D;
		t.askVolume = 0;
		t.bidPrice = 0D;
		t.bidVolume = 0;
		t.avgPrice = 0D;
		t.openPrice = 0D;
		t.highPrice = 0D;
		t.lastPrice = 0D;
		t.lowPrice = 0D;
		t.lowerLimit = 0D;
		t.upperLimit = 0D;
		t.settlePrice = 0D;
		t.preSettlePrice = 0D;
		t.preClosePrice = 0D;
		t.preOpenInterest = 0D;
		t.openInterest = 0D;
		t.openInterestChange = 0;
		t.turnover = 0D;
		t.volume = 0L;
		t.volumeChange = 0;
		
		objPool.add(t);
	}
}
