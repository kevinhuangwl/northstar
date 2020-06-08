package tech.xuanwu.northstar.utils;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.DataProvider;

public class CtpSymbolNameConverterTest {

	@Test(dataProvider = "dp")
	public void f(String n, String s) {
		assertThat(CtpSymbolNameConverter.convert(n)).isEqualTo(s);
	}

	@DataProvider
	public Object[][] dp() {
		return new Object[][] { 
			new Object[] { "rb2010", "螺纹2010" }, 
			new Object[] { "AP010", "苹果2010" },
			new Object[] { "M2010", "豆粕2010" }, 
			new Object[] { "ab2010", "ab2010" }
			};
	}
}
