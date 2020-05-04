package tech.xuanwu.northstar.strategy.client.dataref;

import java.util.List;

import tech.xuanwu.northstar.strategy.client.indicators.Indicator;
import xyz.redtorch.pb.CoreField.BarField;
import xyz.redtorch.pb.CoreField.TickField;

public interface DataRef {

	/**
	 * 更新tick
	 * @param tick
	 */
	void updateTick(TickField tick);
	
	/**
	 * 更新bar
	 * @param bar
	 */
	void updateBar(BarField bar);
	
	/**
	 * 获取tick数据
	 * @param numOfRef	回溯tick数量
	 * @return
	 */
	List<TickField> getTickRef();
	
	/**
	 * 获取bar数据
	 * @param numOfRef	回溯bar数量
	 * @return
	 */
	List<BarField> getBarRef();
	
	/**
	 * 向数据源加入指标，监听数据变化
	 * @param indicator
	 */
	void addIndicator(Indicator indicator);
	
	
	enum PriceType{
		/**
		 * 最高价
		 */
		HIGH,
		/**
		 * 最低价
		 */
		LOW,
		/**
		 * 开盘价
		 */
		OPEN,
		/**
		 * 收盘价
		 */
		CLOSE;
	}
}
