package tech.xuanwu.northstar.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.common.ResultBean;
import tech.xuanwu.northstar.exception.NoSuchAccountException;
import tech.xuanwu.northstar.service.TradeService;
import xyz.redtorch.pb.CoreEnum.DirectionEnum;
import xyz.redtorch.pb.CoreEnum.OffsetFlagEnum;
import xyz.redtorch.pb.CoreEnum.OrderPriceTypeEnum;

@Slf4j
@RestController
@RequestMapping("/trade")
@Api(tags="交易相关操作")
public class TradeController {
	
	@Autowired
	private TradeService tradeService;

	@RequestMapping(value="/order", method=RequestMethod.POST)
	@ApiOperation("账户发送委托单")
	public ResultBean<String> submitOrder(String accountId, String contractSymbol, double price, int volume, DirectionEnum direction,
			OffsetFlagEnum transactionType, OrderPriceTypeEnum priceType) throws Exception{
		
		return new ResultBean<>(tradeService.submitOrder(accountId, contractSymbol, price, volume, direction, transactionType, priceType));
	}
	
	@RequestMapping(value="/cancel", method=RequestMethod.POST)
	@ApiOperation("账户撤销委托单")
	public ResultBean<Void> cancelOrder(String accountId, String orderId) throws Exception{
		tradeService.cancelOrder(accountId, orderId);
		return new ResultBean<>(null);
	}
	
	@RequestMapping(value="/sellout", method=RequestMethod.DELETE)
	@ApiOperation("账户一键全平【危险】")
	public ResultBean<Void> sellOutAllPosition(String accountId) throws NoSuchAccountException{
		log.info("【警告】账户一键全平");
		tradeService.sellOutAllPosition(accountId);
		return new ResultBean<>(null);
	}
}
