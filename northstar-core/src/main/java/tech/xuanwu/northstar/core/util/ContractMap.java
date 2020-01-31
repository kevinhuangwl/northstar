package tech.xuanwu.northstar.core.util;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import xyz.redtorch.pb.CoreField.ContractField;

/**
 * 期货合约工具类，线程安全
 * 
 * @author kevinhuangwl
 *
 */
public class ContractMap{
	
	private ConcurrentHashMap<String, ContractSet> namedContractMap = new ConcurrentHashMap<>(100);
	
	/**
	 * 注册合约
	 * @param c
	 */
	public void registerContract(ContractField c) {
		String symbol = c.getSymbol();
		String shortName = getSymbolShortName(symbol);
		if(!namedContractMap.containsKey(shortName)) {
			namedContractMap.put(shortName, new ContractSet());
		}
		
		namedContractMap.get(shortName).add(c);
		
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
		return namedContractMap.get(shortName).get(symbol);
	}
	
	/**
	 * 通过合约名称获取全月份合约信息
	 * @param symbol
	 * @return
	 */
	public Collection<ContractField> getAllMonthContracts(String symbol){
		String shortName = getSymbolShortName(symbol);
		ContractSet contractSet = namedContractMap.get(shortName);
		return contractSet.getContracts();
	}
	
	/**
	 * 清空集合
	 */
	public void clear() {
		for(Entry<String, ContractSet> e : namedContractMap.entrySet()) {
			e.getValue().clear();
		}
	}
	
	
	static class ContractSet{
		final int INIT_SIZE = 15;
		ConcurrentHashMap<String, ContractField> monthlyContractMap = new ConcurrentHashMap<>(INIT_SIZE);
		
		public void add(ContractField c) {
			String symbol = c.getSymbol();
			monthlyContractMap.put(symbol, c);
		}
		
		public ContractField get(String symbol) {
			return monthlyContractMap.get(symbol);
		}
		
		public Collection<ContractField> getContracts() {
			return monthlyContractMap.values();
		}
		
		public void clear() {
			monthlyContractMap.clear();
		}
	}
}


