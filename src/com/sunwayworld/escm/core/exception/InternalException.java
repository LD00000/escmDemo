package com.sunwayworld.escm.core.exception;

public class InternalException extends RuntimeException {
	private static final long serialVersionUID = 278162256757700703L;
	
	public InternalException(String msg) {
		super(msg);
	}
	
	public InternalException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
	
	public InternalException(Throwable throwable) {
		super(throwable);
	}
}
