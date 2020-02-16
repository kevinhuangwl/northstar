package tech.xuanwu.northstar.core.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.xuanwu.northstar.core.service.TradeService;
import tech.xuanwu.northstar.core.util.FutureDictionary;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.exception.IllegalContractException;
import xyz.redtorch.common.util.UUIDStringPoolUtils;
import xyz.redtorch.pb.CoreEnum.ContingentConditionEnum;
import xyz.redtorch.pb.CoreEnum.DirectionEnum;
import xyz.redtorch.pb.CoreEnum.HedgeFlagEnum;
import xyz.redtorch.pb.CoreEnum.OffsetFlagEnum;
import xyz.redtorch.pb.CoreEnum.OrderPriceTypeEnum;
import xyz.redtorch.pb.CoreEnum.TimeConditionEnum;
import xyz.redtorch.pb.CoreEnum.VolumeConditionEnum;
import xyz.redtorch.pb.CoreField.CancelOrderReqField;
import xyz.redtorch.pb.CoreField.ContractField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;

@Service
public class TradeServiceImpl implements TradeService{
	
	@Autowired
	FutureDictionary ftDict;
	
	@Autowired
	RuntimeEngine rtEngine;

	@Override
	public String submitOrder(String accountName, String contractSymbol, double price, double stopPrice, int volume,
			OrderPriceTypeEnum priceType, DirectionEnum direction, OffsetFlagEnum transactionType,
			HedgeFlagEnum hedgeType, TimeConditionEnum expireType, VolumeConditionEnum volType,
			ContingentConditionEnum trigerType) throws Exception {
		
		checkValuePositive(price);
		checkValuePositive(volume);
		checkValuePositive(stopPrice);
		
		SubmitOrderReqField.Builder sb = SubmitOrderReqField.newBuilder();
		ContractField contract = ftDict.getContractByName(contractSymbol);
		if(contract == null) {
			throw new IllegalContractException("没有找到名称为【"+contractSymbol+"】的合约");
		}
		sb.setContract(contract);
		sb.setPrice(price);
		sb.setStopPrice(stopPrice);
		sb.setVolume(volume);
		sb.setOrderPriceType(priceType);
		sb.setDirection(direction);
		sb.setOffsetFlag(transactionType);
		sb.setHedgeFlag(hedgeType);
		sb.setTimeCondition(expireType);
		sb.setVolumeCondition(volType);
		sb.setContingentCondition(trigerType);
		sb.setMinVolume(1);
		return submitOrder(accountName, sb.build());
	}

	@Override
	public String submitOrder(String accountName, String contractSymbol, double price, int volume, DirectionEnum direction,
			OffsetFlagEnum transactionType) throws Exception {
		checkValuePositive(price);
		checkValuePositive(volume);
		return submitOrder(accountName, contractSymbol, price, 0D, volume, OrderPriceTypeEnum.OPT_LimitPrice, direction, transactionType,
				HedgeFlagEnum.HF_Speculation, TimeConditionEnum.TC_GFD, VolumeConditionEnum.VC_AV, ContingentConditionEnum.CC_Immediately);
	}

	@Override
	public String submitOrder(String accountName, SubmitOrderReqField submitOrderReq) throws Exception{
		IAccount account = getAccount(accountName);
		if(StringUtils.isEmpty(submitOrderReq.getOriginOrderId())) {
			String uuid = UUIDStringPoolUtils.getUUIDString();
			SubmitOrderReqField.Builder sb = submitOrderReq.toBuilder().setOriginOrderId(uuid);
			submitOrderReq = sb.build();
		}
		account.submitOrder(submitOrderReq);
		return submitOrderReq.getOriginOrderId();
	}

	@Override
	public void cancelOrder(String accountName, String originOrderId) {
		CancelOrderReqField.Builder cb = CancelOrderReqField.newBuilder();
		cb.setOriginOrderId(originOrderId);
		cancelOrder(accountName, cb.build());
	}

	@Override
	public void cancelOrder(String accountName, CancelOrderReqField cancelOrderReq) {
		IAccount account = getAccount(accountName);
		account.cancelOrder(cancelOrderReq);
	}

	@Override
	public void sellOutAllPosition(String accountName) {
		IAccount account = getAccount(accountName);
		account.sellOutAllPosition();
	}

	private IAccount getAccount(String accountName) {
		IAccount account = rtEngine.getAccount(accountName);
		if(account == null) {
			throw new IllegalArgumentException("没有找到账户名为【" + accountName + "】的账户");
		}
		return account;
	}
	
	private void checkValuePositive(double val) {
		if(val < 0) {
			throw new IllegalArgumentException("传入的数值不能为负数");
		}
	}
	
}
