package com.employeemanagement.exceptionhandling;

public class NoEmployeeException extends Exception{
    public NoEmployeeException(String message) {
        super(message);
    }
}
