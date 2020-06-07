package tech.xuanwu.northstar.core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.common.ResultBean;
import tech.xuanwu.northstar.common.ReturnCode;
import tech.xuanwu.northstar.entity.ContractInfo;
import tech.xuanwu.northstar.service.MarketDataService;

@Slf4j
@RestController
@RequestMapping("/market")
@Api(tags = "行情相关接口")
public class MarketController {

	@Autowired
	MarketDataService mdService;
	
	@ApiOperation("订阅网关合约")
	@GetMapping(value="/sub")
	public ResultBean<Boolean> subscribe(String gatewayId, String contractName){
		try {
			return new ResultBean<>(mdService.subscribeContract(gatewayId, contractName));
		} catch (Exception e) {
			log.error("", e);
			return new ResultBean<>(ReturnCode.ERROR, e.getMessage());
		}
	}
	
	@ApiOperation("获取可用合约列表")
	@GetMapping(value="/available/future")
	public ResultBean<List<ContractInfo>> getAvailableFutureContracts(String gatewayId){
		try {
			return new ResultBean<>(mdService.getAvailableFutureContracts(gatewayId));
		} catch (Exception e) {
			log.error("", e);
			return new ResultBean<>(ReturnCode.ERROR, e.getMessage());
		}
	}
}
