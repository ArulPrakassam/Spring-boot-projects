package com.employeemanagement.exceptionhandling;


public class AccessForbiddenException extends Exception {
    public AccessForbiddenException(String message) {
        super(message);
    }
}