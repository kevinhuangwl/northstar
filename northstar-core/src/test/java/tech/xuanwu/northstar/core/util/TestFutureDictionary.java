package tech.xuanwu.northstar.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.google.gson.Gson;

import tech.xuanwu.northstar.exception.IllegalContractException;
import xyz.redtorch.pb.CoreEnum.ProductClassEnum;
import xyz.redtorch.pb.CoreField.ContractField;

/**
 * 本单元测试需要直接调用ctp的接口数据来测试
 * @author kevinhuangwl
 *
 */
@SpringBootTest
public class TestFutureDictionary {

	@Autowired
	FutureDictionary dict = new FutureDictionary();
	
	List<ContractField> contractList = new ArrayList<>();
	
	String strRB2005 = "{\"_id\":ObjectId(\"5e42c0869e1683734c1a63fd\"),\"contractId_\":\"rb2005@SHFE@FUTURES@CTP-SimNow724\",\"name_\":\"螺纹钢2005\",\"fullName_\":\"螺纹钢2005\",\"thirdPartyId_\":\"rb2005\",\"unifiedSymbol_\":\"rb2005@SHFE@FUTURES\",\"symbol_\":\"rb2005\",\"exchange_\":4,\"productClass_\":2,\"currency_\":2,\"multiplier_\":10.0,\"priceTick_\":1.0,\"longMarginRatio_\":0.1,\"shortMarginRatio_\":0.1,\"maxMarginSideAlgorithm_\":true,\"underlyingSymbol_\":\"\",\"strikePrice_\":0.0,\"optionsType_\":0,\"underlyingMultiplier_\":0.0,\"lastTradeDateOrContractMonth_\":\"20200515\",\"maxMarketOrderVolume_\":30,\"minMarketOrderVolume_\":1,\"maxLimitOrderVolume_\":500,\"minLimitOrderVolume_\":1,\"combinationType_\":0,\"gatewayId_\":\"CTP-SimNow724\"}";
	String strAP2005 = "{\"_id\":ObjectId(\"5e42c0869e1683734c1a63ff\"),\"contractId_\":\"AP005@CZCE@FUTURES@CTP-SimNow724\",\"name_\":\"苹果005\",\"fullName_\":\"苹果005\",\"thirdPartyId_\":\"AP005\",\"unifiedSymbol_\":\"AP005@CZCE@FUTURES\",\"symbol_\":\"AP005\",\"exchange_\":5,\"productClass_\":2,\"currency_\":2,\"multiplier_\":10.0,\"priceTick_\":1.0,\"longMarginRatio_\":0.07,\"shortMarginRatio_\":0.07,\"maxMarginSideAlgorithm_\":false,\"underlyingSymbol_\":\"\",\"strikePrice_\":0.0,\"optionsType_\":0,\"underlyingMultiplier_\":0.0,\"lastTradeDateOrContractMonth_\":\"20200519\",\"maxMarketOrderVolume_\":200,\"minMarketOrderVolume_\":1,\"maxLimitOrderVolume_\":1000,\"minLimitOrderVolume_\":1,\"combinationType_\":0,\"gatewayId_\":\"CTP-SimNow724\"}";
	String strCU2005 = "{\"_id\":ObjectId(\"5e42c0869e1683734c1a6401\"),\"contractId_\":\"cu2005@SHFE@FUTURES@CTP-SimNow724\",\"name_\":\"铜2005\",\"fullName_\":\"铜2005\",\"thirdPartyId_\":\"cu2005\",\"unifiedSymbol_\":\"cu2005@SHFE@FUTURES\",\"symbol_\":\"cu2005\",\"exchange_\":4,\"productClass_\":2,\"currency_\":2,\"multiplier_\":5.0,\"priceTick_\":10.0,\"longMarginRatio_\":0.09,\"shortMarginRatio_\":0.09,\"maxMarginSideAlgorithm_\":true,\"underlyingSymbol_\":\"\",\"strikePrice_\":0.0,\"optionsType_\":0,\"underlyingMultiplier_\":0.0,\"lastTradeDateOrContractMonth_\":\"20200515\",\"maxMarketOrderVolume_\":30,\"minMarketOrderVolume_\":1,\"maxLimitOrderVolume_\":500,\"minLimitOrderVolume_\":1,\"combinationType_\":0,\"gatewayId_\":\"CTP-SimNow724\"}";
	String strIF2006 = "{\"_id\":ObjectId(\"5e42c0869e1683734c1a6403\"),\"contractId_\":\"IF2006@CFFEX@FUTURES@CTP-SimNow724\",\"name_\":\"沪深300股指2006\",\"fullName_\":\"沪深300股指2006\",\"thirdPartyId_\":\"IF2006\",\"unifiedSymbol_\":\"IF2006@CFFEX@FUTURES\",\"symbol_\":\"IF2006\",\"exchange_\":3,\"productClass_\":2,\"currency_\":2,\"multiplier_\":300.0,\"priceTick_\":0.2,\"longMarginRatio_\":0.1,\"shortMarginRatio_\":0.1,\"maxMarginSideAlgorithm_\":true,\"underlyingSymbol_\":\"\",\"strikePrice_\":0.0,\"optionsType_\":0,\"underlyingMultiplier_\":0.0,\"lastTradeDateOrContractMonth_\":\"20200619\",\"maxMarketOrderVolume_\":10,\"minMarketOrderVolume_\":1,\"maxLimitOrderVolume_\":20,\"minLimitOrderVolume_\":1,\"combinationType_\":0,\"gatewayId_\":\"CTP-SimNow724\"}";
	String strAU2006 = "{\"_id\":ObjectId(\"5e42c0869e1683734c1a6405\"),\"contractId_\":\"au2006@SHFE@FUTURES@CTP-SimNow724\",\"name_\":\"黄金2006\",\"fullName_\":\"黄金2006\",\"thirdPartyId_\":\"au2006\",\"unifiedSymbol_\":\"au2006@SHFE@FUTURES\",\"symbol_\":\"au2006\",\"exchange_\":4,\"productClass_\":2,\"currency_\":2,\"multiplier_\":1000.0,\"priceTick_\":0.02,\"longMarginRatio_\":0.08,\"shortMarginRatio_\":0.08,\"maxMarginSideAlgorithm_\":true,\"underlyingSymbol_\":\"\",\"strikePrice_\":0.0,\"optionsType_\":0,\"underlyingMultiplier_\":0.0,\"lastTradeDateOrContractMonth_\":\"20200615\",\"maxMarketOrderVolume_\":30,\"minMarketOrderVolume_\":1,\"maxLimitOrderVolume_\":500,\"minLimitOrderVolume_\":1,\"combinationType_\":0,\"gatewayId_\":\"CTP-SimNow724\"}";
	
	ContractField falseContract;
	
	@Before
	public void prepareData() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String[] strs = new String[] {strRB2005, strAP2005, strCU2005, strIF2006, strAU2006};
		for(String str : strs) {
			ContractField c = new Gson().fromJson(str, ContractField.class);
			contractList.add(c);
		}
	}
	
	@Test
	public void test_添加合约_正常() throws IllegalContractException {
		//验证正常添加
		for(ContractField c : contractList) {
			dict.add(c);
		}
		
		assertEquals(dict.size(), contractList.size());
		
		//验证重复添加
		for(ContractField c : contractList) {
			dict.add(c);
		}
		
		assertEquals(dict.size(), contractList.size());
	}
	
	@Test(expected = IllegalContractException.class)
	public void test_添加合约_异常_合约类型不正确() throws IllegalContractException {
		ContractField falseContract = contractList.get(ThreadLocalRandom.current().nextInt(contractList.size()));
		falseContract = falseContract.toBuilder().setProductClass(ProductClassEnum.BOND).build();
		
		//验证异常添加
		dict.add(falseContract);
		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test_添加合约_异常_空参数() throws IllegalContractException {
		
		//验证异常添加
		dict.add(null);
		
	}
	
	@Test
	public void test_清空集合() throws IllegalContractException {
		//验证清空
		for(ContractField c : contractList) {
			dict.add(c);
		}
		
		dict.clear();
		
		assertEquals(dict.size(), 0);
	}
	
	@Test
	public void test_查询单个合约_正常用例() throws IllegalContractException {
		for(ContractField c : contractList) {
			dict.add(c);
		}
		
		//使用原有的小写名称查询
		assertSame(dict.getContractByName("rb2005"), contractList.get(0));
		assertSame(dict.getContractByName("cu2005"), contractList.get(2));
		//使用大写统一名称查询
		assertSame(dict.getContractByName("RB2005"), contractList.get(0));
		assertSame(dict.getContractByName("CU2005"), contractList.get(2));
		//使用原有YMM格式查询
		assertSame(dict.getContractByName("AP005"), contractList.get(1));
		//使用统一格式查询
		assertSame(dict.getContractByName("AP2005"), contractList.get(1));
		//原有名称为大写的合约使用小写查询
		assertSame(dict.getContractByName("if2006"), contractList.get(3));
		assertSame(dict.getContractByName("ap2005"), contractList.get(1));
		assertSame(dict.getContractByName("ap005"), contractList.get(1));
		//查询不存在的合约
		assertNull(dict.getContractByName("rb2006"));
		assertNull(dict.getContractByName("ag2006"));
	}
	
//	@Test(expected=IllegalArgumentException.class)
//	public void test_查询单个合约_异常_名称格式错误1() {
//		//使用不正常的格式
//		dict.getContractByName("螺纹钢");
//	}
//	
//	@Test(expected=IllegalArgumentException.class)
//	public void test_查询单个合约_异常_名称格式错误2() {
//		//使用不正常的格式
//		dict.getContractByName("螺纹钢2005");
//	}
//	
//	@Test(expected=IllegalArgumentException.class)
//	public void test_查询单个合约_异常_名称格式错误3() {
//		//使用不正常的格式
//		dict.getContractByName("钢2005");
//	}
//	
//	@Test(expected=IllegalArgumentException.class)
//	public void test_查询单个合约_异常_名称格式错误4() {
//		//使用不正常的格式
//		dict.getContractByName("RB");
//	}
//	
//	@Test(expected=IllegalArgumentException.class)
//	public void test_查询单个合约_异常_名称格式错误5() {
//		//使用不正常的格式
//		dict.getContractByName("RB05");
//	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void test_查询单个合约_异常_空参数() {
		//使用不正常的格式
		dict.getContractByName(null);
	}
	
	@Test
	public void test_查询全月份合约_正常用例() throws IllegalContractException {
		contractList.clear();
		String strRB2005 = "{\"_id\":ObjectId(\"5e42c0869e1683734c1a63fd\"),\"contractId_\":\"rb2005@SHFE@FUTURES@CTP-SimNow724\",\"name_\":\"螺纹钢2005\",\"fullName_\":\"螺纹钢2005\",\"thirdPartyId_\":\"rb2005\",\"unifiedSymbol_\":\"rb2005@SHFE@FUTURES\",\"symbol_\":\"rb2005\",\"exchange_\":4,\"productClass_\":2,\"currency_\":2,\"multiplier_\":10.0,\"priceTick_\":1.0,\"longMarginRatio_\":0.1,\"shortMarginRatio_\":0.1,\"maxMarginSideAlgorithm_\":true,\"underlyingSymbol_\":\"\",\"strikePrice_\":0.0,\"optionsType_\":0,\"underlyingMultiplier_\":0.0,\"lastTradeDateOrContractMonth_\":\"20200515\",\"maxMarketOrderVolume_\":30,\"minMarketOrderVolume_\":1,\"maxLimitOrderVolume_\":500,\"minLimitOrderVolume_\":1,\"combinationType_\":0,\"gatewayId_\":\"CTP-SimNow724\"}";
		String strRB2006 = "{\"_id\":ObjectId(\"5e42c0869e1683734c1a63fd\"),\"contractId_\":\"rb2006@SHFE@FUTURES@CTP-SimNow724\",\"name_\":\"螺纹钢2006\",\"fullName_\":\"螺纹钢2006\",\"thirdPartyId_\":\"rb2006\",\"unifiedSymbol_\":\"rb2006@SHFE@FUTURES\",\"symbol_\":\"rb2006\",\"exchange_\":4,\"productClass_\":2,\"currency_\":2,\"multiplier_\":10.0,\"priceTick_\":1.0,\"longMarginRatio_\":0.1,\"shortMarginRatio_\":0.1,\"maxMarginSideAlgorithm_\":true,\"underlyingSymbol_\":\"\",\"strikePrice_\":0.0,\"optionsType_\":0,\"underlyingMultiplier_\":0.0,\"lastTradeDateOrContractMonth_\":\"20200615\",\"maxMarketOrderVolume_\":30,\"minMarketOrderVolume_\":1,\"maxLimitOrderVolume_\":500,\"minLimitOrderVolume_\":1,\"combinationType_\":0,\"gatewayId_\":\"CTP-SimNow724\"}";
		String strRB2007 = "{\"_id\":ObjectId(\"5e42c0869e1683734c1a63fd\"),\"contractId_\":\"rb2007@SHFE@FUTURES@CTP-SimNow724\",\"name_\":\"螺纹钢2007\",\"fullName_\":\"螺纹钢2007\",\"thirdPartyId_\":\"rb2007\",\"unifiedSymbol_\":\"rb2007@SHFE@FUTURES\",\"symbol_\":\"rb2007\",\"exchange_\":4,\"productClass_\":2,\"currency_\":2,\"multiplier_\":10.0,\"priceTick_\":1.0,\"longMarginRatio_\":0.1,\"shortMarginRatio_\":0.1,\"maxMarginSideAlgorithm_\":true,\"underlyingSymbol_\":\"\",\"strikePrice_\":0.0,\"optionsType_\":0,\"underlyingMultiplier_\":0.0,\"lastTradeDateOrContractMonth_\":\"20200715\",\"maxMarketOrderVolume_\":30,\"minMarketOrderVolume_\":1,\"maxLimitOrderVolume_\":500,\"minLimitOrderVolume_\":1,\"combinationType_\":0,\"gatewayId_\":\"CTP-SimNow724\"}";
		String strRB2008 = "{\"_id\":ObjectId(\"5e42c0869e1683734c1a63fd\"),\"contractId_\":\"rb2008@SHFE@FUTURES@CTP-SimNow724\",\"name_\":\"螺纹钢2008\",\"fullName_\":\"螺纹钢2008\",\"thirdPartyId_\":\"rb2008\",\"unifiedSymbol_\":\"rb2008@SHFE@FUTURES\",\"symbol_\":\"rb2008\",\"exchange_\":4,\"productClass_\":2,\"currency_\":2,\"multiplier_\":10.0,\"priceTick_\":1.0,\"longMarginRatio_\":0.1,\"shortMarginRatio_\":0.1,\"maxMarginSideAlgorithm_\":true,\"underlyingSymbol_\":\"\",\"strikePrice_\":0.0,\"optionsType_\":0,\"underlyingMultiplier_\":0.0,\"lastTradeDateOrContractMonth_\":\"20200815\",\"maxMarketOrderVolume_\":30,\"minMarketOrderVolume_\":1,\"maxLimitOrderVolume_\":500,\"minLimitOrderVolume_\":1,\"combinationType_\":0,\"gatewayId_\":\"CTP-SimNow724\"}";
		String strRB2009 = "{\"_id\":ObjectId(\"5e42c0869e1683734c1a63fd\"),\"contractId_\":\"rb2009@SHFE@FUTURES@CTP-SimNow724\",\"name_\":\"螺纹钢2009\",\"fullName_\":\"螺纹钢2009\",\"thirdPartyId_\":\"rb2009\",\"unifiedSymbol_\":\"rb2009@SHFE@FUTURES\",\"symbol_\":\"rb2009\",\"exchange_\":4,\"productClass_\":2,\"currency_\":2,\"multiplier_\":10.0,\"priceTick_\":1.0,\"longMarginRatio_\":0.1,\"shortMarginRatio_\":0.1,\"maxMarginSideAlgorithm_\":true,\"underlyingSymbol_\":\"\",\"strikePrice_\":0.0,\"optionsType_\":0,\"underlyingMultiplier_\":0.0,\"lastTradeDateOrContractMonth_\":\"20200915\",\"maxMarketOrderVolume_\":30,\"minMarketOrderVolume_\":1,\"maxLimitOrderVolume_\":500,\"minLimitOrderVolume_\":1,\"combinationType_\":0,\"gatewayId_\":\"CTP-SimNow724\"}";
		String[] RBs = new String[] {strRB2005, strRB2006, strRB2007, strRB2008, strRB2009};
		for(String rb : RBs) {
			ContractField c = new Gson().fromJson(rb, ContractField.class);
			contractList.add(c);
			dict.add(c);
		}
		
		//验证原始名称
		Collection<ContractField> result1 = dict.getAllMonthContracts("rb");
		assertEquals(result1.size(), contractList.size());
		//验证统一名称
		Collection<ContractField> result2 = dict.getAllMonthContracts("RB");
		assertEquals(result2.size(), contractList.size());
		
		for(ContractField c : contractList) {
			assertTrue(result1.contains(c));
			assertTrue(result2.contains(c));
		}
		
		//验证不存在
		assertNull(dict.getAllMonthContracts("AG"));
		assertNull(dict.getAllMonthContracts("螺纹钢"));
		assertNull(dict.getAllMonthContracts("螺纹钢2005"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void test_查询全月份合约_异常_合约名称不正确3() {
		dict.getAllMonthContracts(null);
	}
}
