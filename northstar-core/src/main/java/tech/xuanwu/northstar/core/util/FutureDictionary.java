package tech.xuanwu.northstar.core.util;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import tech.xuanwu.northstar.exception.IllegalContractException;
import xyz.redtorch.pb.CoreEnum.ProductClassEnum;
import xyz.redtorch.pb.CoreField.ContractField;

/**
 * 期货字典 该类属于工具类，设计的初衷是解决期货合约查询问题，通过合约名称获取网关返回的可交易合约；在指数合约的场景下，返回可交易的合约列表。
 * 期货合约的名称格式通常为“品种+月份”。其中“品种”由英文字母组成，可能为全大写或者全小写；而“月份”由数字组成，可能为“yyMM”或者“yMM”。
 * 该工具类为了简化合约查询操作，支持“模糊匹配”查询。模糊查询的统一格式为“大写品种代码+yyMM”或“中文名称+yyMM”。
 * 例如合约代码为“rb2005”，统一以“RB2005”查询到结果。
 * 再例如合约代码为“AP005”，统一“AP2005”查询到结果。
 * 
 * @author kevinhuangwl
 *
 */
public class FutureDictionary {

	private ConcurrentHashMap<String, ContractSet> namedContractMap = new ConcurrentHashMap<>(100);

	/**
	 * 添加合约
	 * 
	 * @param c
	 * @throws IllegalContractException
	 */
	public void add(ContractField c) throws IllegalContractException {
		checkNotNullParam(c);
		if (c.getProductClass() != ProductClassEnum.FUTURES) {
			throw new IllegalContractException("期望传入期货合约，实际传入" + c.getProductClass());
		}
		String symbol = c.getSymbol();
		String capName = getSymbolCapitalizedName(symbol);
		if (!namedContractMap.containsKey(capName)) {
			namedContractMap.put(capName, new ContractSet());
		}

		namedContractMap.get(capName).add(c);

	}

	/**
	 * 根据合约名称获取合约信息
	 * 
	 * @param symbol
	 * @return
	 */
	public ContractField getContractByName(String contractNameWithMonth) {
		checkNotNullParam(contractNameWithMonth);
		if(!isValidName(contractNameWithMonth)) {
			throw new IllegalArgumentException("期望合约名称格式为【合约简称yyMM】");
		}
		String capName = getSymbolCapitalizedName(contractNameWithMonth);
		if(!namedContractMap.contains(capName)) {
			return null;
		}
		return namedContractMap.get(capName).get(contractNameWithMonth.toUpperCase());
	}

	/**
	 * 通过合约名称获取全月份合约信息
	 * 
	 * @param symbol
	 * @return
	 */
	public Collection<ContractField> getAllMonthContracts(String contractName) {
		checkNotNullParam(contractName);
		String capName = contractName.toUpperCase();
		ContractSet contractSet = namedContractMap.get(capName);
		if(contractSet==null) {
			return null;
		}
		return contractSet.getContracts();
	}

	/**
	 * 清空字典
	 */
	public void clear() {
		for (Entry<String, ContractSet> e : namedContractMap.entrySet()) {
			e.getValue().clear();
		}
	}
	
	private void checkNotNullParam(Object o) {
		if(o == null) {
			throw new IllegalArgumentException("不允许空参数");
		}
	}
	
	private boolean isValidName(String contractName) {
		if(contractName ==null || contractName.length()<5 || contractName.length()>6) {
			return false;
		}
		if(contractName.replaceAll("(\\d|\\w)", "").length()>0) {
			return false;
		}
		String expectYYMM = contractName.substring(contractName.length()-4);
		if(expectYYMM.replaceAll("\\d", "").length()>0) {
			return false;
		}
		return true;
	}
	
	private String getYearAndMonth(String dateStr_yyyyMMdd) {
		return dateStr_yyyyMMdd.substring(2, 6);
	}

	private String getSymbolCapitalizedName(String symbol) {
		String shortName = symbol.replaceAll("\\d+$", "");
		return shortName.toUpperCase();
	}

	class ContractSet {
		final int INIT_SIZE = 15;
		ConcurrentHashMap<String, ContractField> monthlyContractMap = new ConcurrentHashMap<>(INIT_SIZE);

		public void add(ContractField c) {
			String symbol = c.getSymbol();
			String capName = getSymbolCapitalizedName(symbol);
			String expireDate = c.getLastTradeDateOrContractMonth();
			String expireYearAndMonth = getYearAndMonth(expireDate);
			String unifiedName = capName + expireYearAndMonth;
			monthlyContractMap.put(unifiedName, c);
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
