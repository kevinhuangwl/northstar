package tech.xuanwu.northstar.entity;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import tech.xuanwu.northstar.utils.CtpSymbolNameConverter;
import xyz.redtorch.pb.CoreEnum.ExchangeEnum;
import xyz.redtorch.pb.CoreEnum.PositionDirectionEnum;

@Getter
@Setter
public class PositionVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -323927985465428561L;

	private String name;
	
	private String accountId;
	
	private String unifiedSymbol;
	
	private String direction;
	
	private ExchangeEnum exchange;
	
	private int totalPosition;
	
	private int tdPosition;
	
	private int ydPosition;
	
	private int availablePosition;
	
	private int tdFrozen;
	
	private int ydFrozen;
	
	private String avgOpenPrice;
	
	private int profit; 
	
	public static PositionVO convertFrom(PositionInfo p) {
		PositionVO vo = new PositionVO();
		vo.name = CtpSymbolNameConverter.convert(p.getContract().getSymbol());
		vo.accountId = p.getAccountId();
		vo.unifiedSymbol = p.getContract().getUnifiedSymbol();
		vo.direction = p.getPositionDirection() == PositionDirectionEnum.PD_Long ? "多" : "空";
		vo.exchange = p.getContract().getExchange();
		vo.avgOpenPrice = String.valueOf(p.getOpenPrice());
		vo.profit = (int) p.getOpenPositionProfit();
		vo.tdPosition = p.getTdPosition();
		vo.ydPosition = p.getYdPosition();
		vo.tdFrozen = p.getTdFrozen();
		vo.ydFrozen = p.getYdFrozen();
		vo.totalPosition = vo.tdPosition + vo.ydPosition;
		vo.availablePosition = vo.totalPosition - vo.tdFrozen - vo.ydFrozen;
		return vo;
	}
}
