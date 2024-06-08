package com.projet6.PayMyBuddy.exception;

public class SelfConnectionException extends RuntimeException {
    public SelfConnectionException(String message) {
        super(message);
    }
}