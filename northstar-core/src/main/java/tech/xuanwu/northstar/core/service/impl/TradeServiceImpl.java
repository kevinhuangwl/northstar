package tech.xuanwu.northstar.core.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.xuanwu.northstar.core.persistence.repo.ContractRepo;
import tech.xuanwu.northstar.core.persistence.repo.GatewayRepo;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.entity.ContractInfo;
import tech.xuanwu.northstar.exception.NoSuchAccountException;
import tech.xuanwu.northstar.exception.NoSuchContractException;
import tech.xuanwu.northstar.exception.TradeException;
import tech.xuanwu.northstar.service.TradeService;
import xyz.redtorch.common.util.UUIDStringPoolUtils;
import xyz.redtorch.pb.CoreEnum.ContingentConditionEnum;
import xyz.redtorch.pb.CoreEnum.DirectionEnum;
import xyz.redtorch.pb.CoreEnum.ForceCloseReasonEnum;
import xyz.redtorch.pb.CoreEnum.HedgeFlagEnum;
import xyz.redtorch.pb.CoreEnum.OffsetFlagEnum;
import xyz.redtorch.pb.CoreEnum.OrderPriceTypeEnum;
import xyz.redtorch.pb.CoreEnum.TimeConditionEnum;
import xyz.redtorch.pb.CoreEnum.VolumeConditionEnum;
import xyz.redtorch.pb.CoreField.CancelOrderReqField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;

@Service
public class TradeServiceImpl implements TradeService{
	
	@Autowired
	ContractRepo contractRepo;
	
	@Autowired
	GatewayRepo gatewayRepo;
	
	@Autowired
	RuntimeEngine rtEngine;

	@Override
	public String submitOrder(String accountId, String contractSymbol, double price, double stopPrice, int volume,
			OrderPriceTypeEnum priceType, DirectionEnum direction, OffsetFlagEnum transactionType,
			HedgeFlagEnum hedgeType, TimeConditionEnum expireType, VolumeConditionEnum volType,
			ContingentConditionEnum trigerType) throws Exception {
		
		checkValuePositive(price);
		checkValuePositive(volume);
		checkValuePositive(stopPrice);
		
		String[] ids = accountId.split("@");
		String gatewayId = ids[1];
		
		SubmitOrderReqField.Builder sb = SubmitOrderReqField.newBuilder();
		ContractInfo contract = contractRepo.getContractBySymbol(gatewayId, contractSymbol);
		if(contract == null) {
			throw new NoSuchContractException(contractSymbol);
		}
		sb.setContract(contract.convertTo());
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
		sb.setGatewayId(gatewayId);
		return submitOrder(accountId, sb.build());
	}

	@Override
	public String submitOrder(String accountId, String contractSymbol, double price, int volume, DirectionEnum direction,
			OffsetFlagEnum transactionType, OrderPriceTypeEnum priceType) throws Exception{
		checkValuePositive(price);
		checkValuePositive(volume);
		return submitOrder(accountId, contractSymbol, price, 0D, volume, priceType, direction, transactionType,
				HedgeFlagEnum.HF_Speculation, TimeConditionEnum.TC_GFD, VolumeConditionEnum.VC_AV, ContingentConditionEnum.CC_Immediately);
	}

	@Override
	public String submitOrder(String accountId, SubmitOrderReqField submitOrderReq) throws NoSuchAccountException, TradeException{
		IAccount account = rtEngine.getAccount(accountId);
		SubmitOrderReqField.Builder sb = submitOrderReq.toBuilder();
		submitOrderReq = sb.build();
		String uuid = StringUtils.isEmpty(sb.getOriginOrderId()) ? UUIDStringPoolUtils.getUUIDString() : sb.getOriginOrderId();
		sb.setOriginOrderId(uuid);

		if(sb.getContingentCondition() == ContingentConditionEnum.CC_Unkonwn) {
			sb.setContingentCondition(ContingentConditionEnum.CC_Immediately);
		}
		if(sb.getHedgeFlag() == HedgeFlagEnum.HF_Unknown) {
			sb.setHedgeFlag(HedgeFlagEnum.HF_Speculation);
		}
		if(sb.getTimeCondition() == TimeConditionEnum.TC_Unkonwn) {
			sb.setTimeCondition(TimeConditionEnum.TC_GFD);
		}
		if(sb.getVolumeCondition() == VolumeConditionEnum.VC_Unkonwn) {
			sb.setVolumeCondition(VolumeConditionEnum.VC_AV);
		}
		if(sb.getForceCloseReason() == ForceCloseReasonEnum.FCR_Unkonwn) {
			sb.setForceCloseReason(ForceCloseReasonEnum.FCR_NotForceClose);
		}
		
		account.submitOrder(sb.build());
		return uuid;
	}

	@Override
	public void cancelOrder(String accountId, String orderId) throws NoSuchAccountException, TradeException {
		CancelOrderReqField.Builder cb = CancelOrderReqField.newBuilder();
		cb.setOrderId(orderId);
		cancelOrder(accountId, cb.build());
	}

	@Override
	public void cancelOrder(String accountId, CancelOrderReqField cancelOrderReq) throws NoSuchAccountException, TradeException {
		IAccount account = rtEngine.getAccount(accountId);
		account.cancelOrder(cancelOrderReq);
	}

	@Override
	public void sellOutAllPosition(String accountId) throws NoSuchAccountException {
		IAccount account = rtEngine.getAccount(accountId);
		account.sellOutAllPosition();
	}
	
	private void checkValuePositive(double val) {
		if(val < 0) {
			throw new IllegalArgumentException("传入的数值不能为负数");
		}
	}
	
}
