package com.javatraining.notification_mgmt.exception.custom;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}