package tech.xuanwu.northstar.core.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import tech.xuanwu.northstar.core.service.TradeService;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import xyz.redtorch.common.util.UUIDStringPoolUtils;
import xyz.redtorch.pb.CoreEnum.ContingentConditionEnum;
import xyz.redtorch.pb.CoreEnum.DirectionEnum;
import xyz.redtorch.pb.CoreEnum.HedgeFlagEnum;
import xyz.redtorch.pb.CoreEnum.OffsetFlagEnum;
import xyz.redtorch.pb.CoreEnum.OrderPriceTypeEnum;
import xyz.redtorch.pb.CoreEnum.TimeConditionEnum;
import xyz.redtorch.pb.CoreEnum.VolumeConditionEnum;
import xyz.redtorch.pb.CoreField.ContractField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;

@ContextConfiguration(classes=TradeServiceImpl.class)
@TestExecutionListeners(listeners = MockitoTestExecutionListener.class)
public class TestTradeServiceImpl extends AbstractTestNGSpringContextTests  {
	
	
	@MockBean
	RuntimeEngine rtEngine;

	//这是真实要测试的bean
	@Autowired
	TradeService tradeService;
	
	final String ACC_NAME = "CTP";
	
	@Test
	public void testSubmitOrder_Normal() throws Exception {
		//准备数据
		ContractField.Builder cb = ContractField.newBuilder();
		SubmitOrderReqField.Builder sb = SubmitOrderReqField.newBuilder();
		String originOrderId = UUIDStringPoolUtils.getUUIDString();
		cb.setSymbol(ACC_NAME);
		ContractField contract = cb.build();
		OrderPriceTypeEnum priceType = OrderPriceTypeEnum.OPT_AnyPrice;
		DirectionEnum direction = DirectionEnum.D_Buy;
		OffsetFlagEnum transactionType = OffsetFlagEnum.OF_Close;
		HedgeFlagEnum hedgeType = HedgeFlagEnum.HF_Hedge;
		TimeConditionEnum expireType = TimeConditionEnum.TC_GFS;
		VolumeConditionEnum volType = VolumeConditionEnum.VC_CV;
		ContingentConditionEnum trigerType = ContingentConditionEnum.CC_AskPriceGreaterEqualStopPrice;
		sb.setOriginOrderId(originOrderId);
		sb.setContract(contract);
		sb.setPrice(10000.33);
		sb.setStopPrice(999.77);
		sb.setVolume(1002);
		sb.setOrderPriceType(priceType);
		sb.setDirection(direction);
		sb.setOffsetFlag(transactionType);
		sb.setHedgeFlag(hedgeType);
		sb.setTimeCondition(expireType);
		sb.setVolumeCondition(volType);
		sb.setContingentCondition(trigerType);
		SubmitOrderReqField submitOrderReq = sb.build();
		
		IAccount mockAccount = mock(IAccount.class);
		given(rtEngine.getAccount(ACC_NAME)).willReturn(mockAccount);
		
		//测试预封装接口
		String orderId = tradeService.submitOrder(ACC_NAME, submitOrderReq);
		assertThat(orderId).as("测试正确合约名称【%s】", ACC_NAME).isEqualTo(originOrderId);
		
		ArgumentCaptor<SubmitOrderReqField> argCaptor = ArgumentCaptor.forClass(SubmitOrderReqField.class);
		verify(mockAccount).submitOrder(argCaptor.capture());
		
		assertThat(submitOrderReq).isEqualTo(argCaptor.getValue());
		
	}
	
	
	
	public void testCancelOrder() {
		
	}
}
