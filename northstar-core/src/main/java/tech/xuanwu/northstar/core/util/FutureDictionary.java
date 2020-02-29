package tech.xuanwu.northstar.core.util;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import tech.xuanwu.northstar.exception.NoSuchContractException;
import xyz.redtorch.pb.CoreEnum.ProductClassEnum;
import xyz.redtorch.pb.CoreField.ContractField;

/**
 * 期货字典 该类属于工具类，设计的初衷是解决期货合约查询问题，通过合约名称获取网关返回的可交易合约；在指数合约的场景下，返回可交易的合约列表。
 * 期货合约的名称格式通常为“品种+月份”。其中“品种”由英文字母组成，可能为全大写或者全小写；而“月份”由数字组成，可能为“yyMM”或者“yMM”。
 * 该工具类为了简化合约查询操作，支持“模糊匹配”查询。模糊查询的统一格式为“大写品种代码+yyMM”或“中文名称+yyMM”。
 * 例如合约代码为“rb2005”，统一以“RB2005”查询到结果，原始合约代码仍然有效。
 * 再例如合约代码为“AP005”，统一“AP2005”查询到结果，原始合约代码仍然有效。
 * 
 * @author kevinhuangwl
 *
 */
public class FutureDictionary {

	ConcurrentHashMap<String, ContractSet> namedContractMap = new ConcurrentHashMap<>(100);
	
	/**
	 * 添加合约
	 * 
	 * @param c
	 * @throws NoSuchContractException
	 */
	public void add(ContractField c) throws NoSuchContractException {
		checkNotNullParam(c);
		if (c.getProductClass() != ProductClassEnum.FUTURES) {
			throw new NoSuchContractException("期望传入期货合约，实际传入" + c.getProductClass());
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
	 * @throws NoSuchContractException 
	 */
	public ContractField getContractByName(String contractNameWithMonth) {
		checkNotNullParam(contractNameWithMonth);
		String capName = getSymbolCapitalizedName(contractNameWithMonth);
		String strYMM = contractNameWithMonth.substring(contractNameWithMonth.length()-3);
		if(!namedContractMap.containsKey(capName) || namedContractMap.get(capName).get(strYMM)==null) {
			return null;
		}
		return namedContractMap.get(capName).get(strYMM);
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
	
	/**
	 * 期货合约总数
	 */
	public int size() {
		int totalSize = 0;
		for(Entry<String, ContractSet> e : namedContractMap.entrySet()) {
			totalSize += e.getValue().getContracts().size();
		}
		return totalSize;
	}
	
	private void checkNotNullParam(Object o) {
		if(o == null) {
			throw new IllegalArgumentException("不允许空参数");
		}
	}
	
	private String getYearAndMonth(String dateStr_yyyyMMdd) {
		return dateStr_yyyyMMdd.substring(3, 6);
	}

	private String getSymbolCapitalizedName(String symbol) {
		String shortName = symbol.replaceAll("\\d+$", "");
		return shortName.toUpperCase();
	}

	/**
	 * 每个品种是一个对象，内部用"yMM->合约"的方式保存
	 * @author kevinhuangwl
	 *
	 */
	class ContractSet {
		final int INIT_SIZE = 15;
		final int KEY_LEN = 3;
		ConcurrentHashMap<String, ContractField> monthlyContractMap = new ConcurrentHashMap<>(INIT_SIZE);

		public void add(ContractField c) {
			String expireDate = c.getLastTradeDateOrContractMonth();
			String expireYearAndMonth = getYearAndMonth(expireDate);
			monthlyContractMap.put(expireYearAndMonth, c);
		}

		public ContractField get(String monthKey) {
			if(monthKey.length()!=KEY_LEN) {
				throw new IllegalArgumentException("传入的【"+monthKey+"】为非法参数");
			}
			return monthlyContractMap.get(monthKey);
		}

		public Collection<ContractField> getContracts() {
			return monthlyContractMap.values();
		}

		public void clear() {
			monthlyContractMap.clear();
		}
	}

}
