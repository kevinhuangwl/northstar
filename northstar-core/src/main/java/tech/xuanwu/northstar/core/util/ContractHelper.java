package tech.xuanwu.northstar.core.util;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;
import xyz.redtorch.pb.CoreField.ContractField;

/**
 * 期货合约工具类，线程安全
 * @author kevinhuangwl
 *
 */
@Slf4j
public class ContractHelper {
	
	private ConcurrentHashMap<String, ContractSet> contractMap = new ConcurrentHashMap<>(100);

	/**
	 * 注册合约
	 * 过期合约会自动淘汰，无须手工维护
	 * @param c
	 */
	public void registerContract(ContractField c) {
		String symbol = c.getSymbol();
		String shortName = getSymbolShortName(symbol);
		if(!contractMap.containsKey(shortName)) {
			contractMap.put(shortName, new ContractSet());
		}
		
		contractMap.get(shortName).unifiedAppend(c);
		
	}
	
	private String getSymbolShortName(String symbol) {
		String shortName = symbol.replaceAll("\\d+", "");
		return shortName.toUpperCase();
	}
	
	/**
	 * 根据合约名称获取合约信息
	 * @param symbol
	 * @return
	 */
	public ContractField getContractBySymbol(String symbol) {
		String shortName = getSymbolShortName(symbol);
		ContractSet contractSet = contractMap.get(shortName);
		
		return contractSet.getContract(symbol);
	}
	
	/**
	 * 通过合约名称获取全月份合约信息
	 * @param symbol
	 * @return
	 */
	public Collection<ContractField> getAllMonthContracts(String symbol){
		String shortName = getSymbolShortName(symbol);
		ContractSet contractSet = contractMap.get(shortName);
		return contractSet.getContracts();
	}
	
	/**
	 * 由于期货合约的数量是衡定的，因此可以用月份来做重复筛选
	 * @author kevinhuangwl
	 *
	 */
	static class ContractSet{
		final int INIT_SIZE = 12;
		ConcurrentHashMap<String, ContractField> monthlyContractMap = new ConcurrentHashMap<>(INIT_SIZE);
		ConcurrentHashMap<String, ContractField> symbolMap = new ConcurrentHashMap<>(INIT_SIZE);
		
		public void unifiedAppend(ContractField c) {
			String symbol = c.getSymbol();
			log.info("登记合约：{}", symbol);
			String month = symbol.substring(symbol.length() - 2, symbol.length());
			ContractField oldContract = monthlyContractMap.put(month, c);
			symbolMap.put(symbol, c);
			if(oldContract!=null && !symbol.equals(oldContract.getSymbol())) {
				symbolMap.remove(oldContract.getSymbol());
				log.info("移除旧合约：{}", oldContract.getSymbol());
			}
		}
		
		public ContractField getContract(String symbol) {
			return symbolMap.get(symbol);
		}
		
		public Collection<ContractField> getContracts() {
			return monthlyContractMap.values();
		}
	}
}


