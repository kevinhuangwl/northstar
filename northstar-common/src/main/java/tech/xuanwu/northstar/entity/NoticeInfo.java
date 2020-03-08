package tech.xuanwu.northstar.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class NoticeInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1410936298829451910L;

	private String event;
	
	private String message;
	
	private Object data;
}
