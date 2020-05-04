package tech.xuanwu.northstar.strategy.client.algo;

public interface RunningAlgo {
	
	void init(double[] data, int nextUpdateCursor);

	void update(double val);
	
	double getResult();
}
