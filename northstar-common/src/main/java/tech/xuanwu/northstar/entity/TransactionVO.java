package tech.xuanwu.northstar.entity;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import tech.xuanwu.northstar.utils.CtpSymbolNameConverter;
import xyz.redtorch.pb.CoreEnum.DirectionEnum;
import xyz.redtorch.pb.CoreEnum.OffsetFlagEnum;

@Getter
@Setter
public class TransactionVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9107091587075123082L;
	
	private String name;
	
	private String action;
	
	private String tradePrice;
	
	private int tradeVolume;
	
	private String tradeTime;

	public static TransactionVO convertFrom(TransactionInfo t) {
		TransactionVO vo = new TransactionVO();
		vo.name = CtpSymbolNameConverter.convert(t.getContract().getSymbol());
		vo.action = (t.getDirection() == DirectionEnum.D_Buy ? "买" : "卖") + (t.getOffsetFlag() == OffsetFlagEnum.OF_Open ? "开" : t.getOffsetFlag() == OffsetFlagEnum.OF_CloseToday ? "平今" : "平");
		vo.tradePrice = String.valueOf(t.getPrice());
		vo.tradeVolume = t.getVolume();
		vo.tradeTime = t.getTradeTime();
		return vo;
	}
}
