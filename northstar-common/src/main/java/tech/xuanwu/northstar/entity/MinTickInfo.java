package tech.xuanwu.northstar.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import lombok.Data;
import xyz.redtorch.pb.CoreField.TickField;

/**
 * 为了提高存储效率，Tick数据按分钟分桶存储
 * @author kevinhuangwl
 *
 */
@Data
public class MinTickInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7404736956412213362L;
	private String unifiedSymbol;  // 统一合约标识
	private String gatewayId; // 网关ID
	private String tradingDay;  // 交易日
	private String actionDay;  // 业务发生日
	private String actionTimeMin;  // 时间(HHmm)
	private double preClosePrice;  // 前收盘价
	private double preSettlePrice;  // 昨结算价
	private double preOpenInterest;// 昨持仓
	private double settlePrice;  // 结算价
	private double upperLimit;  // 涨停价
	private double lowerLimit;  // 跌停价 
	private List<TickInfo> ticks = new ArrayList<>(180);	//按1分钟TICK数量初始化
	
	
	public static MinTickInfo convertFrom(TickField tick) {
		MinTickInfo minTick = new MinTickInfo();
		TickField.Builder tb = tick.toBuilder();
		BeanUtils.copyProperties(tb, minTick);
		minTick.setActionTimeMin(tb.getActionTime().substring(0, 4));
		return minTick;
	}
	
	@Data
	static class TickInfo implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 6044830705093050929L;
		private String actionTime;  // 时间(HHmmssSSS)
		private long actionTimestamp;  // 时间戳
		private int status;  // 状态
		private double lastPrice;  // 最新成交价
		private double avgPrice;  // 均价
		private long totalBidVol;  // 定单买入总量
		private long totalAskVol;  // 定单卖出总量
		private double weightedAvgBidPrice;  // 加权平均委买价格
		private double weightedAvgAskPrice;  // 加权平均卖价格
		private double iopv;  // 净值估值
		private double yieldToMaturity;   // 到期收益率
		private long volumeDelta;  // 成交量变化
		private long volume;  // 总成交量
		private double turnover;  // 成交总额
		private double turnoverDelta;  // 成交总额变化
		private long numTrades;  // 成交笔数
		private long numTradesDelta;  // 成交笔数
		private double openInterest;  // 持仓量
		private double openInterestDelta;  // 持仓量变化
		private double openPrice;  // 开盘价
		private double highPrice;  // 最高价
		private double lowPrice;  // 最低价
		private double bidPrice;  // 买1价
		private double askPrice;  // 卖1价
		private int bidVolume;  // 买1量
		private int askVolume;  // 卖1量
		
		public static TickInfo convertFrom(TickField tick) {
			TickInfo tickInfo = new TickInfo();
			TickField.Builder tb = TickField.newBuilder();
			BeanUtils.copyProperties(tb, tickInfo);
			tickInfo.setBidPrice(tb.getBidPrice(0));
			tickInfo.setBidVolume(tb.getBidVolume(0));
			tickInfo.setAskPrice(tb.getAskPrice(0));
			tickInfo.setAskVolume(tb.getAskVolume(0));
			
			return tickInfo;
		}
	}
}
