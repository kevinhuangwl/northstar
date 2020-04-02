package tech.xuanwu.northstar.core.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import static tech.xuanwu.northstar.constant.CommonConstant.$$;
import tech.xuanwu.northstar.entity.ContractInfo;
import xyz.redtorch.pb.CoreField.ContractField;
import xyz.redtorch.pb.CoreField.TickField;

public class IndexContract {

	private final ContractInfo self = new ContractInfo();

	private TickField.Builder tickBuilder = TickField.newBuilder();

	/* 记录合约对应的数组下标 */
	private Map<String, Integer> symbolIndexMap;
	/* 记录合约的数组下标 */
	private Set<Integer> memSet;

	private int contractSize;

	private TickEventHandler tickHandler;

	private double[] prices;
	private double[] upperLimits;
	private double[] lowerLimits;
	private long[] volumeDeltas;
	private long[] volumes;
	private double[] openInterestDeltas;
	private double[] openInterests;
	private volatile double totalOpenInterest;

	public IndexContract(String indexSymbol, List<ContractInfo> seriesContracts, TickEventHandler tickHandler) {
		this.tickHandler = tickHandler;
		ContractInfo proto = seriesContracts.get(0);
		BeanUtils.copyProperties(proto, self);
		String chnName = proto.getName().replaceAll("\\d+", "");
		String indexSymbolSuffix = indexSymbol.replaceAll("\\w+", "");
		String symbol = indexSymbol; // 代码
		String name = chnName + indexSymbolSuffix; // 简称
		String fullName = name; // 全称
		String thirdPartyId = symbol; // 第三方ID
		String unifiedSymbol = symbol + $$ + proto.getExchange() + $$ + proto.getProductClass(); // 统一ID，通常是
																									// <合约代码@交易所代码@产品类型>
		String contractId = unifiedSymbol + $$ + proto.getGatewayId(); // ID，通常是 <合约代码@交易所代码@产品类型@网关ID>
		self.setContractId(contractId);
		self.setUnifiedSymbol(unifiedSymbol);
		self.setThirdPartyId(thirdPartyId);
		self.setFullName(fullName);
		self.setName(name);
		self.setSymbol(symbol);

		contractSize = seriesContracts.size();
		symbolIndexMap = new HashMap<>(contractSize);
		memSet = new HashSet<>(contractSize);
		prices = new double[contractSize];
		volumeDeltas = new long[contractSize];
		volumes = new long[contractSize];
		openInterestDeltas = new double[contractSize];
		openInterests = new double[contractSize];
		upperLimits = new double[contractSize];
		lowerLimits = new double[contractSize];

		for (int i = 0; i < seriesContracts.size(); i++) {
			symbolIndexMap.put(seriesContracts.get(i).getUnifiedSymbol(), i);
		}

		tickBuilder.setUnifiedSymbol(unifiedSymbol);
		tickBuilder.setGatewayId(proto.getGatewayId());
	}

	public ContractField getContract() {
		return self.convertTo();
	}

	public synchronized void updateByTick(TickField tick) {
		Integer contractIndex = symbolIndexMap.get(tick.getUnifiedSymbol());
		if (memSet.contains(contractIndex)) {
			tickHandler.onTick(calculate());
			// 重置缓存
			memSet.clear();
			totalOpenInterest = 0;
		}

		memSet.add(contractIndex);
		totalOpenInterest += tick.getOpenInterest();
		prices[contractIndex] = tick.getLastPrice();
		volumeDeltas[contractIndex] = tick.getVolumeDelta();
		volumes[contractIndex] = tick.getVolume();
		openInterestDeltas[contractIndex] = tick.getOpenInterestDelta();
		openInterests[contractIndex] = tick.getOpenInterest();
	}

	private TickField calculate() {
		// 加权均价
		double weightedPrice = 0;
		// 加权涨停价
		double weightedUpperLimit = 0;
		// 加权跌停价
		double weightedLowerLimit = 0;
		// 合计成交量变化
		long totalVolumeDeltaInTick = 0;
		// 合计成交量
		long totalVolume = 0;
		// 合计持仓量变化
		double totalOpenInterestDeltaInTick = 0;

		for (int i = 0; i < contractSize; i++) {
			weightedPrice += prices[i] * openInterests[i] / totalOpenInterest;
			weightedLowerLimit += lowerLimits[i] * openInterests[i] / totalOpenInterest;
			weightedUpperLimit += upperLimits[i] * openInterests[i] / totalOpenInterest;
			totalVolumeDeltaInTick += volumeDeltas[i];
			totalVolume += volumes[i];
			totalOpenInterestDeltaInTick += openInterestDeltas[i];
		}
		tickBuilder.setLastPrice(weightedPrice);
		if (tickBuilder.getHighPrice()==0 || tickBuilder.getHighPrice() < weightedPrice) {
			tickBuilder.setHighPrice(weightedPrice);
		}
		if (tickBuilder.getLowPrice()==0 || tickBuilder.getLowPrice() > weightedPrice) {
			tickBuilder.setLowPrice(weightedPrice);
		}
		if(tickBuilder.getOpenPrice()==0) {
			tickBuilder.setOpenPrice(weightedPrice);
		}
		if(tickBuilder.getUpperLimit()==0) {
			tickBuilder.setUpperLimit(weightedUpperLimit);
		}
		if(tickBuilder.getLowerLimit()==0) {
			tickBuilder.setLowerLimit(weightedLowerLimit);
		}
		tickBuilder.setVolume(totalVolume);
		tickBuilder.setVolumeDelta(totalVolumeDeltaInTick);
		tickBuilder.setOpenInterestDelta(totalOpenInterestDeltaInTick);
		tickBuilder.setOpenInterest(totalOpenInterest);
		return tickBuilder.build();
	}

	public interface TickEventHandler {

		void onTick(TickField tick);
	}
}
