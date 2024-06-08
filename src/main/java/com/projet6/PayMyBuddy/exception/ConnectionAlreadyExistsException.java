package com.projet6.PayMyBuddy.exception;

public class ConnectionAlreadyExistsException extends RuntimeException {
    public ConnectionAlreadyExistsException(String message) {
        super(message);
    }
}