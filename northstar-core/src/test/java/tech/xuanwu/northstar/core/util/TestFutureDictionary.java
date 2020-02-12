package tech.xuanwu.northstar.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
	public void test_添加合约_异常() throws IllegalContractException {
		ContractField falseContract = contractList.get(ThreadLocalRandom.current().nextInt(contractList.size()));
		falseContract = falseContract.toBuilder().setProductClass(ProductClassEnum.BOND).build();
		
		//验证异常添加
		dict.add(falseContract);
		
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
	public void test_查询单个合约() throws IllegalContractException {
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
	}
	
	@Test
	public void test_查询全月份合约() {
		
	}
}
