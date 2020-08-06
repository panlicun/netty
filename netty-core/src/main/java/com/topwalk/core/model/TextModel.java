package com.topwalk.core.model;

import java.io.Serializable;

public class TextModel extends BaseModel implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1425307876096494974L;
	
	private String message;// 文本
	private long message_size; //文本总长度
	private boolean recvieSuccess; //是否接收成功

	public TextModel(){}

	public TextModel(String message) {
		this.message = message;
		this.message_size = message.length();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
		this.message_size = message.length();
	}

	public long getMessage_size() {
		return message_size;
	}

	public void setMessage_size(long message_size) {
		this.message_size = message_size;
	}

	public boolean isRecvieSuccess() {
		return recvieSuccess;
	}

	public void setRecvieSuccess(boolean recvieSuccess) {
		this.recvieSuccess = recvieSuccess;
	}
}
