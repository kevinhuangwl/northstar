package tech.xuanwu.northstar.core.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix="simulate-account-setting")
@Component
public class SimulateAccountSettings {

	/**
	 * 交易手续费（单位：tick价位）
	 * 比如值为1时，代表手续费为1个TICK价值
	 */
	private int commissionTick;
	
	/**
	 * 占用保证金（单位：百分数）
	 * 比如值为1时，在交易所保证金的基础上加1%
	 */
	private int marginRateExtra;
}
