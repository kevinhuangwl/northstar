package xyz.redtorch.common.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

import org.testng.annotations.Test;

import tech.xuanwu.northstar.entity.MinTickInfo;
import xyz.redtorch.pb.CoreField.TickField;

public class MinTickInfoTest {
	private String unifiedSymbol = "rb2010";  // 统一合约标识
	private String gatewayId = "simnow724"; // 网关ID
	private String tradingDay = "20100505";  // 交易日
	private String actionDay = "20200505";  // 业务发生日
	private double preClosePrice = 10000.22;  // 前收盘价
	private double preSettlePrice = 65554.35;  // 昨结算价
	private double preOpenInterest = 102111532;// 昨持仓
	private double settlePrice = 123456688;  // 结算价
	private double upperLimit = 3244546;  // 涨停价
	private double lowerLimit = 4613215;  // 跌停价
	
	@Test
	public void test() {
		TickField.Builder tb = TickField.newBuilder();
		tb.setActionDay(actionDay);
		tb.setUnifiedSymbol(unifiedSymbol);
		tb.setGatewayId(gatewayId);
		tb.setTradingDay(tradingDay);
		tb.setPreClosePrice(preClosePrice);
		tb.setPreSettlePrice(preSettlePrice);
		tb.setPreOpenInterest(preOpenInterest);
		tb.setSettlePrice(settlePrice);
		tb.setUpperLimit(upperLimit);
		tb.setLowerLimit(lowerLimit);
		
		MinTickInfo tickInfo = MinTickInfo.convertFrom(tb.build());
		assertThat(tickInfo.getActionDay()).isEqualTo(actionDay);
		assertThat(tickInfo.getUnifiedSymbol()).isEqualTo(unifiedSymbol);
		assertThat(tickInfo.getGatewayId()).isEqualTo(gatewayId);
		assertThat(tickInfo.getTradingDay()).isEqualTo(tradingDay);
		assertThat(tickInfo.getPreClosePrice()).isCloseTo(preClosePrice, offset(0.00001));
		assertThat(tickInfo.getPreOpenInterest()).isCloseTo(preOpenInterest, offset(0.00001));
		assertThat(tickInfo.getPreSettlePrice()).isCloseTo(preSettlePrice, offset(0.00001));
		assertThat(tickInfo.getSettlePrice()).isCloseTo(settlePrice, offset(0.00001));
		assertThat(tickInfo.getUpperLimit()).isCloseTo(upperLimit, offset(0.00001));
		assertThat(tickInfo.getLowerLimit()).isCloseTo(lowerLimit, offset(0.00001));
	}
}
