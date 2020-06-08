package tech.xuanwu.northstar.utils;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class CtpSymbolNameConverter {

	private static final String metaData = "{\"AG\":\"沪银\",\"AL\":\"沪铝\",\"AU\":\"沪金\",\"BU\":\"沥青\",\"CU\":\"沪铜\",\"RB\":\"螺纹\",\"RU\":\"橡胶\",\"ZN\":\"沪锌\",\"NI\":\"沪镍\",\"A\":\"豆一\",\"B\":\"豆二\",\"C\":\"玉米\",\"I\":\"铁矿\",\"J\":\"焦煤\",\"L\":\"塑料\",\"M\":\"豆粕\",\"P\":\"棕榈油\",\"PP\":\"PP\",\"Y\":\"豆油\",\"CF\":\"郑棉\",\"FG\":\"玻璃\",\"ME\":\"郑醇\",\"SR\":\"白糖\",\"JD\":\"鸡蛋\",\"AP\":\"苹果\"}";

	private static Map<String, String> keyMap = new Gson().fromJson(metaData, HashMap.class);
	
	private CtpSymbolNameConverter() {}
	
	public static String convert(String symbol) {
		String symbolName = symbol.replaceAll("\\d+", "");
		String symbolMonth = symbol.replace(symbolName, "");
		symbolName = symbolName.toUpperCase();
		if(!keyMap.containsKey(symbolName)) {
			return symbol;
		}
		symbolMonth = symbolMonth.length() == 3 ? "2" + symbolMonth : symbolMonth;
		return keyMap.get(symbolName) + symbolMonth;
	}
}
