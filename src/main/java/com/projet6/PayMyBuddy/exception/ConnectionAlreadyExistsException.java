package com.projet6.PayMyBuddy.exception;

public class ConnectionAlreadyExistsException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConnectionAlreadyExistsException(String message) {
        super(message);
    }
}