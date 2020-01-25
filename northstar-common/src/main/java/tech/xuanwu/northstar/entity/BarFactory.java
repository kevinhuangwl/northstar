package tech.xuanwu.northstar.entity;

import java.util.concurrent.ConcurrentLinkedQueue;

import xyz.redtorch.pb.CoreField.BarField;

public class BarFactory {

	static final ConcurrentLinkedQueue<Bar> objPool = new ConcurrentLinkedQueue<Bar>();
	
	public static Bar getBarAs(BarField bar) {
		Bar b = objPool.poll();
		if(b == null) {
			b = new Bar();
		}
		
		b.dataSourceId = bar.getDataSourceId();
		b.tradingDay = bar.getTradingDay();
		b.actionDay = bar.getActionDay();
		b.actionTime = bar.getActionTime();
		b.actionTimestamp = bar.getActionTimestamp();
		b.openPrice = bar.getOpenPrice();
		b.highPrice = bar.getHighPrice();
		b.lowPrice = bar.getLowPrice();
		b.closePrice = bar.getClosePrice();
		b.openInterest = bar.getOpenInterest();
		b.openInterestChange = bar.getOpenInterestChange();
		b.volume = bar.getVolume();
		b.volumeChange = bar.getVolumeChange();
		b.turnover = bar.getTurnover();
		b.turnoverChange = bar.getTurnoverChange();
		
		return b;
	}
	
	
	public static void releaseBar(Bar b) {
		b.dataSourceId = "";
		b.tradingDay = "";
		b.actionDay = "";
		b.actionTime = "";
		b.actionTimestamp = 0L;
		b.openPrice = 0D;
		b.highPrice = 0D;
		b.lowPrice = 0D;
		b.closePrice = 0D;
		b.openInterest = 0D;
		b.openInterestChange = 0D;
		b.volume = 0L;
		b.volumeChange = 0L;
		b.turnover = 0D;
		b.turnoverChange = 0D;
		
		objPool.add(b);
	}
}
