package tech.xuanwu.northstar.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.common.ResultBean;
import tech.xuanwu.northstar.core.service.TradeService;
import tech.xuanwu.northstar.exception.NoSuchAccountException;
import xyz.redtorch.pb.CoreEnum.DirectionEnum;
import xyz.redtorch.pb.CoreEnum.OffsetFlagEnum;

@Slf4j
@RestController
@RequestMapping("/trade")
public class TradeController {
	
	@Autowired
	private TradeService tradeService;

	@RequestMapping(value="/order", method=RequestMethod.POST)
	public ResultBean<String> submitOrder(String accountName, String contractSymbol, double price, int volume, DirectionEnum direction,
			OffsetFlagEnum transactionType){
		
		try {
			return new ResultBean<>(tradeService.submitOrder(accountName, contractSymbol, price, volume, direction, transactionType));
		} catch (Exception e) {
			return new ResultBean<String>(ResultBean.ReturnCode.ERROR, e.getMessage());
		}
	}
	
	@RequestMapping(value="/cancel", method=RequestMethod.DELETE)
	public ResultBean<Void> cancelOrder(String accountName, String originOrderId){
		try {
			tradeService.cancelOrder(accountName, originOrderId);
		} catch (NoSuchAccountException e) {
			return new ResultBean<>(ResultBean.ReturnCode.ERROR, e.getMessage());
		}
		return new ResultBean(Void.TYPE);
	}
	
	@RequestMapping(value="/sellout", method=RequestMethod.POST)
	public ResultBean<Void> sellOutAllPosition(String accountName){
		log.info("【警告】账户一键全平");
		try {
			tradeService.sellOutAllPosition(accountName);
		} catch (NoSuchAccountException e) {
			return new ResultBean<>(ResultBean.ReturnCode.ERROR, e.getMessage());
		}
		return new ResultBean(Void.TYPE);
	}
}
