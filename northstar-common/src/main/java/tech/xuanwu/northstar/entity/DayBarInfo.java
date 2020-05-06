package tech.xuanwu.northstar.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import lombok.Data;
import xyz.redtorch.pb.CoreField.BarField;

/**
 * 为了提高存储效率，Bar数据按天分桶存储
 * @author kevinhuangwl
 *
 */
@Data
public class DayBarInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1577972145528048331L;
	private String unifiedSymbol;  // 统一合约标识
	private String gatewayId; // 网关ID
	private String tradingDay;  // 交易日
	
	private double preOpenInterest;// 昨持仓
	private double preClosePrice;  // 前收盘价
	private double preSettlePrice;  // 昨结算价
	private List<BarInfo> bars = new ArrayList<>(420); //按1分钟K线的数量初始化
	
	public static DayBarInfo convertFrom(BarField bar) {
		DayBarInfo dayBar = new DayBarInfo();
		BarField.Builder bb = bar.toBuilder();
		BeanUtils.copyProperties(bb, dayBar);
		return dayBar;
	}
	
	public void addBar(BarField bar) {
		bars.add(BarInfo.convertFrom(bar));
	}
	
	@Data
	static class BarInfo implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 8491173108789585432L;
		private String actionDay;  // 业务发生日
		private String actionTime;  // 时间(HHmmssSSS)
		private long actionTimestamp;  // 时间戳
		private double openPrice;  // 开
		private double highPrice;  // 高
		private double lowPrice;  // 低
		private double closePrice;  // 收
		private double openInterest;  // 最后持仓量
		private double openInterestDelta;  // 持仓量（Bar）
		private long volume;  // 最后总成交量
		private long volumeDelta;  // 成交量（Bar）
		private double turnover;  // 最后成交总额
		private double turnoverDelta;  // 成交总额（Bar）
		private long numTrades;  // 最新成交笔数
		private long numTradesDelta;  // 成交笔数（Bar）
		
		public static BarInfo convertFrom(BarField bar) {
			BarInfo barInfo = new BarInfo();
			BeanUtils.copyProperties(bar, barInfo);
			return barInfo;
			
		}
	}
}
