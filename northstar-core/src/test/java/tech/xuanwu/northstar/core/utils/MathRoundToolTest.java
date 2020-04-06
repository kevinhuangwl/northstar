package tech.xuanwu.northstar.core.utils;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.DataProvider;

public class MathRoundToolTest {
  @Test(dataProvider = "dp")
  public void f(double in, double priceTick, double out) {
	  assertThat(roundWithPriceTick(in, priceTick)).isEqualTo(out);
  }
  
  private double roundWithPriceTick(double price, double priceTick) {
	  int enlargePrice = (int) (price * 1000);
	  int enlargePriceTick = (int) (priceTick * 1000);
	  int numOfTicks = enlargePrice / enlargePriceTick;
	  int tickCarry = (enlargePrice % enlargePriceTick) < (enlargePriceTick / 2) ? 0 : 1;
	  
	  return  enlargePriceTick * (numOfTicks + tickCarry) * 1.0 / 1000;
  }

  @DataProvider
  public Object[][] dp() {
    return new Object[][] {
      new Object[] { 1.1, 1.0, 1 },
      new Object[] { 1.5, 1.0, 2 },
      new Object[] { 1.9, 1.0, 2 },
      new Object[] { 2.1, 1.0, 2 },
      new Object[] { 1.1, 0.5, 1 },
      new Object[] { 1.4, 0.5, 1.5 },
      new Object[] { 1.75, 0.5, 2 },
      new Object[] { 1.749, 0.5, 1.5 },
      new Object[] { 1.18, 0.1, 1.2 },
      new Object[] { 1.15, 0.1, 1.2 },
      new Object[] { 1.11, 0.1, 1.1 },
      new Object[] { 1.11, 0.2, 1.2 },
      new Object[] { 1.15, 0.2, 1.2 },
      new Object[] { 1.18, 0.2, 1.2 },
      new Object[] { 1.119, 0.01, 1.12 },
      new Object[] { 1.115, 0.01, 1.12 },
      new Object[] { 1.112, 0.01, 1.11 },
      new Object[] { 11211, 10, 11210 },
      new Object[] { 11215, 10, 11220 },
      new Object[] { 11219, 10, 11220 }
    };
  }
}
