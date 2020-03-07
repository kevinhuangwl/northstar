package tech.xuanwu.northstar.entity;

import java.io.Serializable;

import lombok.Data;
import xyz.redtorch.pb.CoreEnum.CombinationTypeEnum;
import xyz.redtorch.pb.CoreEnum.CurrencyEnum;
import xyz.redtorch.pb.CoreEnum.ExchangeEnum;
import xyz.redtorch.pb.CoreEnum.OptionsTypeEnum;
import xyz.redtorch.pb.CoreEnum.ProductClassEnum;
import xyz.redtorch.pb.CoreField.ContractField;

@Data
public class ContractInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6053841212965950545L;
	
	String contractId;  // ID，通常是  <合约代码@交易所代码@产品类型@网关ID>
	String name;  // 简称
	String fullName;  // 全称
	String thirdPartyId;  // 第三方ID
	String unifiedSymbol;  // 统一ID，通常是 <合约代码@交易所代码@产品类型>
	String symbol;  // 代码
	ExchangeEnum exchange;  // 交易所
	ProductClassEnum productClass;  // 产品类型
	CurrencyEnum currency;  // 币种
	double multiplier;  // 合约乘数
	double priceTick;  // 最小变动价位
	double longMarginRatio;  // 多头保证金率
	double shortMarginRatio;  // 空头保证金率
	boolean maxMarginSideAlgorithm;  // 最大单边保证金算法
	String underlyingSymbol;  // 基础商品代码
	double strikePrice;  // 执行价
	OptionsTypeEnum optionsType;  // 期权类型
	double underlyingMultiplier;  // 合约基础商品乘数
	String lastTradeDateOrContractMonth;  // 最后交易日或合约月
	int maxMarketOrderVolume;  // 市价单最大下单量
	int minMarketOrderVolume;  // 市价单最小下单量
	int maxLimitOrderVolume;  // 限价单最大下单量
	int minLimitOrderVolume;  // 限价单最小下单量
	CombinationTypeEnum combinationType; // 组合类型
	String gatewayId;  // 网关
	boolean isSubscribed; 	//是否已订阅
	
	public static ContractInfo convertFrom(ContractField c) {
		ContractInfo info = new ContractInfo();
		info.contractId = c.getContractId();
		info.name = c.getName();
		info.fullName = c.getFullName();
		info.thirdPartyId = c.getThirdPartyId();
		info.unifiedSymbol = c.getUnifiedSymbol();
		info.symbol = c.getSymbol();
		info.exchange = c.getExchange();
		info.productClass = c.getProductClass();
		info.currency = c.getCurrency();
		info.multiplier = c.getMultiplier();
		info.priceTick = c.getPriceTick();
		info.longMarginRatio = c.getLongMarginRatio();
		info.shortMarginRatio = c.getShortMarginRatio();
		info.maxMarginSideAlgorithm = c.getMaxMarginSideAlgorithm();
		info.underlyingSymbol = c.getUnderlyingSymbol();
		info.strikePrice = c.getStrikePrice();
		info.optionsType = c.getOptionsType();
		info.underlyingMultiplier = c.getUnderlyingMultiplier();
		info.lastTradeDateOrContractMonth = c.getLastTradeDateOrContractMonth();
		info.maxMarketOrderVolume = c.getMaxMarketOrderVolume();
		info.minMarketOrderVolume = c.getMinMarketOrderVolume();
		info.maxLimitOrderVolume = c.getMaxLimitOrderVolume();
		info.minLimitOrderVolume = c.getMinLimitOrderVolume();
		info.combinationType = c.getCombinationType();
		info.gatewayId = c.getGatewayId();
		return info;
	}
	
	public ContractField convertTo() {
		ContractField.Builder cb = ContractField.newBuilder();
		cb.setContractId(contractId);
		cb.setName(name);
		cb.setFullName(fullName);
		cb.setThirdPartyId(thirdPartyId);
		cb.setUnifiedSymbol(unifiedSymbol);
		cb.setSymbol(symbol);
		cb.setExchange(exchange);
		cb.setProductClass(productClass);
		cb.setCurrency(currency);
		cb.setCombinationType(combinationType);
		cb.setLastTradeDateOrContractMonth(lastTradeDateOrContractMonth);
		cb.setUnderlyingSymbol(underlyingSymbol);
		cb.setLongMarginRatio(longMarginRatio);
		cb.setMaxMarginSideAlgorithm(maxMarginSideAlgorithm);
		cb.setMaxLimitOrderVolume(maxLimitOrderVolume);
		cb.setMinLimitOrderVolume(minLimitOrderVolume);
		cb.setMaxMarketOrderVolume(maxMarketOrderVolume);
		cb.setMinMarketOrderVolume(minMarketOrderVolume);
		cb.setMultiplier(multiplier);
		cb.setOptionsType(optionsType);
		cb.setPriceTick(priceTick);
		cb.setShortMarginRatio(shortMarginRatio);
		cb.setStrikePrice(strikePrice);
		cb.setUnderlyingMultiplier(underlyingMultiplier);
		cb.setGatewayId(gatewayId);
		return cb.build();
	}

}
