package tech.xuanwu.northstar.strategy.client.indicators;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.util.Assert;

import tech.xuanwu.northstar.strategy.client.algo.RunningMeanAlgo;
import tech.xuanwu.northstar.strategy.client.dataref.DataRef;
import xyz.redtorch.pb.CoreField.BarField;

/**
 * 移动平均线
 * @author kevinhuangwl
 *
 */
public class MovingAverageIndicator extends BaseIndicator implements Indicator{

	private RunningMeanAlgo algo;
	
	private double[] computedVal;
	private int nextComputedCursor;
	private int sampleSize;
	private int maxRefLen;
	
	private DataRef dataRef;
	private DataRef.PriceType refPriceType;
	
	public MovingAverageIndicator(DataRef dataRef, DataRef.PriceType priceType, int sampleSize) {
		this(dataRef, priceType, sampleSize, DEFAULT_LEN);
	}
	
	public MovingAverageIndicator(DataRef dataRef, DataRef.PriceType priceType, int sampleSize, int maxRef) {
		this.dataRef = dataRef;
		this.refPriceType = priceType;
		this.sampleSize = sampleSize;
		this.maxRefLen = maxRef + 1;
		algo = new RunningMeanAlgo(sampleSize);
		dataRef.addIndicator(this);
	}
	
	@Override
	public void init() {
		List<BarField> barData = dataRef.getBarRef();
		computedVal = new double[maxRefLen];
		double[] data = new double[sampleSize];
		Assert.isTrue(barData.size() >= sampleSize, "数据源的数据量不足");
		List<BarField> srcBarData = barData.subList(barData.size() - sampleSize, barData.size());
		Method method = null;
		try {			
			switch(refPriceType){
			case HIGH:
				method = BarField.class.getMethod("getHighPrice");
				break;
			case LOW:
				method = BarField.class.getMethod("getLowPrice");
				break;
			case OPEN:
				method = BarField.class.getMethod("getOpenPrice");
				break;
			case CLOSE:
				method = BarField.class.getMethod("getClosePrice");
				break;
			default:
				throw new IllegalStateException();	
			}
			
			for(int i=0; i<sampleSize; i++) {
				BarField bar = srcBarData.get(i);
				double price = (double) method.invoke(bar);
				data[i] = price;
			}
			
		}catch(Exception e) {
			throw new IllegalStateException(e);
		}
		
		algo.init(data, 0);
	}

	@Override
	public double getValue() {
		return getValue(0);
	}

	@Override
	public double getValue(int ref) {
		Assert.isTrue(ref>=0, "回溯长度不能为负数");
		Assert.isTrue(maxRefLen>=ref, "回溯限期超过计算长度");
		return computedVal[(nextComputedCursor - 1 - ref) % maxRefLen];
	}

	@Override
	public void update(double v) {
		algo.update(v);
		computedVal[nextComputedCursor++ % maxRefLen] = algo.getResult();
	}

	@Override
	public int getMaxRef() {
		return Math.min(maxRefLen - 1, nextComputedCursor);
	}
	
}
