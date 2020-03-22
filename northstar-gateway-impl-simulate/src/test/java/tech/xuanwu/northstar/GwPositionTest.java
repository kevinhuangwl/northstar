package tech.xuanwu.northstar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.gson.Gson;

import tech.xuanwu.northstar.entity.ContractInfo;
import tech.xuanwu.northstar.entity.PositionInfo;
import tech.xuanwu.northstar.entity.TransactionInfo;
import xyz.redtorch.pb.CoreField.ContractField;
import xyz.redtorch.pb.CoreField.TradeField;

public class GwPositionTest {
  
	GwPosition position;
	
	@BeforeMethod
	public void beforeTest() {
		String pjson = "{\n" + 
				"            \"positionId\": \"rb2005@SHFE@FUTURES@PD_Long@HF_Speculation@094020@CNY@CTP-SimNow724\",\n" + 
				"            \"accountId\": \"094020@CNY@CTP-SimNow724\",\n" + 
				"            \"positionDirection\": \"PD_Long\",\n" + 
				"            \"position\": 5,\n" + 
				"            \"frozen\": 0,\n" + 
				"            \"ydPosition\": 0,\n" + 
				"            \"ydFrozen\": 0,\n" + 
				"            \"tdPosition\": 5,\n" + 
				"            \"tdFrozen\": 0,\n" + 
				"            \"lastPrice\": 3548.0,\n" + 
				"            \"price\": 3546.8,\n" + 
				"            \"priceDiff\": 1.2,\n" + 
				"            \"openPrice\": 3546.8,\n" + 
				"            \"openPriceDiff\": 1.199999999999818,\n" + 
				"            \"positionProfit\": 60.0,\n" + 
				"            \"positionProfitRatio\": 0.00338409475465313,\n" + 
				"            \"openPositionProfit\": 59.999999999990905,\n" + 
				"            \"openPositionProfitRatio\": 0.003384094754652617,\n" + 
				"            \"useMargin\": 17730.0,\n" + 
				"            \"exchangeMargin\": 17730.0,\n" + 
				"            \"contractValue\": 177400.0,\n" + 
				"            \"contract\": {\n" + 
				"                \"contractId\": \"rb2005@SHFE@FUTURES@CTP-SimNow724\",\n" + 
				"                \"name\": \"螺纹钢2005\",\n" + 
				"                \"fullName\": \"螺纹钢2005\",\n" + 
				"                \"thirdPartyId\": \"rb2005\",\n" + 
				"                \"unifiedSymbol\": \"rb2005@SHFE@FUTURES\",\n" + 
				"                \"symbol\": \"rb2005\",\n" + 
				"                \"exchange\": \"SHFE\",\n" + 
				"                \"productClass\": \"FUTURES\",\n" + 
				"                \"currency\": \"CNY\",\n" + 
				"                \"multiplier\": 10.0,\n" + 
				"                \"priceTick\": 1.0,\n" + 
				"                \"longMarginRatio\": 0.1,\n" + 
				"                \"shortMarginRatio\": 0.1,\n" + 
				"                \"maxMarginSideAlgorithm\": true,\n" + 
				"                \"underlyingSymbol\": \"\",\n" + 
				"                \"strikePrice\": 0.0,\n" + 
				"                \"optionsType\": \"O_Unknown\",\n" + 
				"                \"underlyingMultiplier\": 0.0,\n" + 
				"                \"lastTradeDateOrContractMonth\": \"20200515\",\n" + 
				"                \"maxMarketOrderVolume\": 30,\n" + 
				"                \"minMarketOrderVolume\": 1,\n" + 
				"                \"maxLimitOrderVolume\": 500,\n" + 
				"                \"minLimitOrderVolume\": 1,\n" + 
				"                \"combinationType\": \"COMBT_Unknown\",\n" + 
				"                \"gatewayId\": \"CTP-SimNow724\",\n" + 
				"                \"subscribed\": false\n" + 
				"            },\n" + 
				"            \"gatewayId\": \"CTP-SimNow724\"\n" + 
				"        }";
		PositionInfo p = new Gson().fromJson(pjson, PositionInfo.class);
		position = new GwPosition(p);
	}
	
	@Test(dataProvider = "trade1")
	public void testAddPosition(TradeField tradeField) {
		position.addPosition(tradeField);
		//
		assertThat(position.getPosition().getPosition()).isEqualTo(8);
		assertThat(position.getPosition().getTdPosition()).isEqualTo(8);
		assertThat(position.getPosition().getOpenPrice()).isEqualTo(3555.125);
	}
	
	@Test(dataProvider = "trade2")
	public void testReducePosition(TradeField tradeField) {
		position.reducePosition(tradeField);
		
		assertThat(position.getPosition().getPosition()).isEqualTo(2);
		assertThat(position.getPosition().getTdPosition()).isEqualTo(2);
		assertThat(position.getPosition().getOpenPrice()).isEqualTo(3546.8);
	}
	
	public void testUpdateByTick() {
		
	}
	
	public void testProceedDailySettlement() {
		
	}
	
	@DataProvider
	public Object[][] trade1() {
		String tradeStr = "{\n" + 
				"            \"tradeId\": \"CTP-SimNow724@1_1752316581_265@D_Buy@6443\",\n" + 
				"            \"adapterTradeId\": \"1_1752316581_265@D_Buy@6443\",\n" + 
				"            \"adapterOrderId\": \"1_1752316581_265\",\n" + 
				"            \"originOrderId\": \"e98d8880-5d04-4cf2-ac75-8a47552240f2\",\n" + 
				"            \"orderId\": \"CTP-SimNow724@1_1752316581_265\",\n" + 
				"            \"orderLocalId\": \"12036\",\n" + 
				"            \"brokerOrderSeq\": \"14687\",\n" + 
				"            \"orderSysId\": \"10554\",\n" + 
				"            \"settlementId\": \"1\",\n" + 
				"            \"sequenceNo\": \"17190\",\n" + 
				"            \"accountId\": \"094020@CNY@CTP-SimNow724\",\n" + 
				"            \"direction\": \"D_Buy\",\n" + 
				"            \"offsetFlag\": \"OF_Open\",\n" + 
				"            \"price\": 3569.0,\n" + 
				"            \"volume\": 3,\n" + 
				"            \"tradingDay\": \"20200318\",\n" + 
				"            \"tradeDate\": \"20200317\",\n" + 
				"            \"tradeTime\": \"21:45:23\",\n" + 
				"            \"tradeTimestamp\": 0,\n" + 
				"            \"gatewayId\": \"CTP-SimNow724\"\n" + 
				"        }";
		
		String contractStr = "{\n" + 
				"                \"contractId\": \"rb2005@SHFE@FUTURES@CTP-SimNow724\",\n" + 
				"                \"name\": \"螺纹钢2005\",\n" + 
				"                \"fullName\": \"螺纹钢2005\",\n" + 
				"                \"thirdPartyId\": \"rb2005\",\n" + 
				"                \"unifiedSymbol\": \"rb2005@SHFE@FUTURES\",\n" + 
				"                \"symbol\": \"rb2005\",\n" + 
				"                \"exchange\": \"SHFE\",\n" + 
				"                \"productClass\": \"FUTURES\",\n" + 
				"                \"currency\": \"CNY\",\n" + 
				"                \"multiplier\": 10.0,\n" + 
				"                \"priceTick\": 1.0,\n" + 
				"                \"longMarginRatio\": 0.1,\n" + 
				"                \"shortMarginRatio\": 0.1,\n" + 
				"                \"maxMarginSideAlgorithm\": true,\n" + 
				"                \"underlyingSymbol\": \"\",\n" + 
				"                \"strikePrice\": 0.0,\n" + 
				"                \"optionsType\": \"O_Unknown\",\n" + 
				"                \"underlyingMultiplier\": 0.0,\n" + 
				"                \"lastTradeDateOrContractMonth\": \"20200515\",\n" + 
				"                \"maxMarketOrderVolume\": 30,\n" + 
				"                \"minMarketOrderVolume\": 1,\n" + 
				"                \"maxLimitOrderVolume\": 500,\n" + 
				"                \"minLimitOrderVolume\": 1,\n" + 
				"                \"combinationType\": \"COMBT_Unknown\",\n" + 
				"                \"gatewayId\": \"CTP-SimNow724\",\n" + 
				"                \"subscribed\": false\n" + 
				"            }" ;
		ContractInfo contract = new Gson().fromJson(contractStr, ContractInfo.class);
		TransactionInfo trade = new Gson().fromJson(tradeStr, TransactionInfo.class);
		trade.setContract(contract);
		return new Object[][] { new Object[] {trade.convertTo()}};
	}
	
	@DataProvider
	public Object[][] trade2() {
		String tradeStr = "{\n" + 
				"            \"tradeId\": \"CTP-SimNow724@1_1752316581_265@D_Buy@6443\",\n" + 
				"            \"adapterTradeId\": \"1_1752316581_265@D_Buy@6443\",\n" + 
				"            \"adapterOrderId\": \"1_1752316581_265\",\n" + 
				"            \"originOrderId\": \"e98d8880-5d04-4cf2-ac75-8a47552240f2\",\n" + 
				"            \"orderId\": \"CTP-SimNow724@1_1752316581_265\",\n" + 
				"            \"orderLocalId\": \"12036\",\n" + 
				"            \"brokerOrderSeq\": \"14687\",\n" + 
				"            \"orderSysId\": \"10554\",\n" + 
				"            \"settlementId\": \"1\",\n" + 
				"            \"sequenceNo\": \"17190\",\n" + 
				"            \"accountId\": \"094020@CNY@CTP-SimNow724\",\n" + 
				"            \"direction\": \"D_Buy\",\n" + 
				"            \"offsetFlag\": \"OF_Open\",\n" + 
				"            \"price\": 3569.0,\n" + 
				"            \"volume\": 3,\n" + 
				"            \"tradingDay\": \"20200318\",\n" + 
				"            \"tradeDate\": \"20200317\",\n" + 
				"            \"tradeTime\": \"21:45:23\",\n" + 
				"            \"tradeTimestamp\": 0,\n" + 
				"            \"gatewayId\": \"CTP-SimNow724\"\n" + 
				"        }";
		
		String contractStr = "{\n" + 
				"                \"contractId\": \"rb2005@SHFE@FUTURES@CTP-SimNow724\",\n" + 
				"                \"name\": \"螺纹钢2005\",\n" + 
				"                \"fullName\": \"螺纹钢2005\",\n" + 
				"                \"thirdPartyId\": \"rb2005\",\n" + 
				"                \"unifiedSymbol\": \"rb2005@SHFE@FUTURES\",\n" + 
				"                \"symbol\": \"rb2005\",\n" + 
				"                \"exchange\": \"SHFE\",\n" + 
				"                \"productClass\": \"FUTURES\",\n" + 
				"                \"currency\": \"CNY\",\n" + 
				"                \"multiplier\": 10.0,\n" + 
				"                \"priceTick\": 1.0,\n" + 
				"                \"longMarginRatio\": 0.1,\n" + 
				"                \"shortMarginRatio\": 0.1,\n" + 
				"                \"maxMarginSideAlgorithm\": true,\n" + 
				"                \"underlyingSymbol\": \"\",\n" + 
				"                \"strikePrice\": 0.0,\n" + 
				"                \"optionsType\": \"O_Unknown\",\n" + 
				"                \"underlyingMultiplier\": 0.0,\n" + 
				"                \"lastTradeDateOrContractMonth\": \"20200515\",\n" + 
				"                \"maxMarketOrderVolume\": 30,\n" + 
				"                \"minMarketOrderVolume\": 1,\n" + 
				"                \"maxLimitOrderVolume\": 500,\n" + 
				"                \"minLimitOrderVolume\": 1,\n" + 
				"                \"combinationType\": \"COMBT_Unknown\",\n" + 
				"                \"gatewayId\": \"CTP-SimNow724\",\n" + 
				"                \"subscribed\": false\n" + 
				"            }" ;
		ContractInfo contract = new Gson().fromJson(contractStr, ContractInfo.class);
		TransactionInfo trade = new Gson().fromJson(tradeStr, TransactionInfo.class);
		trade.setContract(contract);
		return new Object[][] { new Object[] { trade.convertTo() } };
	}
}
