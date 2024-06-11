package com.projet6.PayMyBuddy.exception;

public class SelfConnectionException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SelfConnectionException(String message) {
        super(message);
    }
}