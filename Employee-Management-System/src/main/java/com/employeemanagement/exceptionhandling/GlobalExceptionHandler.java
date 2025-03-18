package com.employeemanagement.exceptionhandling;

import com.employeemanagement.model.ResponseObject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoEmployeeException.class)
    public ResponseEntity<ResponseObject<Object>> handleNoEmployeeException(NoEmployeeException e) {

        ResponseObject<Object> response = new ResponseObject<>(false, HttpStatus.NOT_FOUND.value(), e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseObject<Object>> handleGeneralException(Exception e) {

        ResponseObject<Object> response = new ResponseObject<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(),"An error occurred: Internal Server Error");


        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle validation errors for @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseObject<Object>> handleValidationExceptions(MethodArgumentNotValidException e) {
        String errors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ResponseObject<Object> response = new ResponseObject<>(false, HttpStatus.BAD_REQUEST.value(), "Bad Request. "+errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle validation errors for @RequestParam, @PathVariable
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseObject<Object>> handleConstraintViolationException(ConstraintViolationException e) {
        String errors = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        ResponseObject<Object> response = new ResponseObject<>(false, HttpStatus.BAD_REQUEST.value(), "Bad Request. "+errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
