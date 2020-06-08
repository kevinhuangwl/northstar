package tech.xuanwu.northstar.entity;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import tech.xuanwu.northstar.utils.CtpSymbolNameConverter;
import xyz.redtorch.pb.CoreEnum.PositionDirectionEnum;

@Getter
@Setter
public class PositionVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -323927985465428561L;

	private String name;
	
	private String direction;
	
	private int totalPosition;
	
	private int availablePosition;
	
	private String avgOpenPrice;
	
	private int profit; 
	
	
	public static PositionVO convertFrom(PositionInfo p) {
		PositionVO vo = new PositionVO();
		vo.name = CtpSymbolNameConverter.convert(p.getContract().getSymbol());
		String positionId = p.getPositionId();
		vo.direction = positionId.split("@")[3].equals(PositionDirectionEnum.PD_Long.toString()) ? "多" : "空";
		vo.totalPosition = p.getPosition();
		vo.availablePosition = p.getTdPosition() + p.getYdPosition();
		vo.avgOpenPrice = String.valueOf(p.getOpenPrice());
		vo.profit = (int) p.getOpenPositionProfit();
		return vo;
	}
}
