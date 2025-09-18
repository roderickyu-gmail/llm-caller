package com.hoocta.core;

public class ServiceException extends Exception {
	private static final long serialVersionUID = 1114120958963983122L;
	private int code;
	private String[] array;

	public ServiceException(Throwable cause, int code, String... array) {
		super(cause);
		this.code = code;
		this.array = array;
	}

	public ServiceException(int code, String... array) {
		this.code = code;
		this.array = array;
	}
	public ServiceException(int code) {
		this.code = code;
	}


	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String[] getArray() {
		return array;
	}

	public void setArray(String[] array) {
		this.array = array;
	}
}
