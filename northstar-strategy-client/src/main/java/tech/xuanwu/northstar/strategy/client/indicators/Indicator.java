package tech.xuanwu.northstar.strategy.client.indicators;

public interface Indicator {

	void init();
	
	void update(double v);
	
	double getValue();
	
	double getValue(int ref);
	
	int getMaxRef();
}
