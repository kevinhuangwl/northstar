package tech.xuanwu.northstar.core.dao.mongo;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import tech.xuanwu.northstar.common.ResultBean;
import xyz.redtorch.common.mongo.MongoDBUtils;

@SpringBootTest
public class TestMongoDBUtils {

	@Test
	public void testDocumentToBean() {
		//测试普通对象
		String data = "测试数据";
		String msg = "成功了";
		Integer code = Integer.valueOf(10);
		ResultBean<String> resultBean = new ResultBean(data);
		resultBean.setMsg(msg);
		
		try {
			Document doc = MongoDBUtils.beanToDocument(resultBean);
			assertEquals(doc.get("data", String.class), resultBean.getData());
			assertEquals(doc.get("msg", String.class), resultBean.getMsg());
			assertEquals(doc.get("rtnCode", Integer.class), Integer.valueOf(resultBean.getRtnCode()));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testBeanToDocument() {
		String data = "测试数据";
		String msg = "成功了";
		Integer code = Integer.valueOf(10);
		
		//测试普通对象
		Document doc = new Document();
		doc.put("data", data);
		doc.put("msg", msg);
		doc.put("rtnCode", code);
		ResultBean<String> resultBean = new ResultBean(1, "");
		try {
			resultBean = MongoDBUtils.documentToBean(doc, resultBean);
			assertEquals(resultBean.getData(), data);
			assertEquals(resultBean.getMsg(), msg);
			assertEquals(Integer.valueOf(resultBean.getRtnCode()), code);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		
	}
}
