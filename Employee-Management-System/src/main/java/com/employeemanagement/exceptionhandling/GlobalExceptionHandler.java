package com.employeemanagement.exceptionhandling;

import com.employeemanagement.dto.ResponseObject;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    //handle if employee data is not found
    @ExceptionHandler(NoEmployeeException.class)
    public ResponseEntity<ResponseObject<Object>> handleNoEmployeeException(NoEmployeeException e) {

        ResponseObject<Object> response = new ResponseObject<>(false, HttpStatus.NOT_FOUND.value(), e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    //when trying to access restricted resource
    @ExceptionHandler(AccessForbiddenException.class)
    public ResponseEntity<ResponseObject<Object>> handleAccessForBiddenException(AccessForbiddenException e) {

        ResponseObject<Object> response = new ResponseObject<>(false, HttpStatus.FORBIDDEN.value(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // Handle validation errors for @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseObject<Object>> handleValidationExceptions(MethodArgumentNotValidException e) {
//        String errors = e.getBindingResult().getFieldErrors()
//                .stream()
//                .map(DefaultMessageSourceResolvable::getDefaultMessage)
//                .collect(Collectors.joining(", "));

        ResponseObject<Object> response = new ResponseObject<>(false, HttpStatus.BAD_REQUEST.value(), "Bad Request");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle validation errors for @RequestParam, @PathVariable
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseObject<Object>> handleConstraintViolationException(ConstraintViolationException e) {
//        String errors = e.getConstraintViolations()
//                .stream()
//                .map(ConstraintViolation::getMessage)
//                .collect(Collectors.joining(", "));

        ResponseObject<Object> response = new ResponseObject<>(false, HttpStatus.BAD_REQUEST.value(), "Bad Request");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //handle if the registering user is already there
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ResponseObject<Object>> handleInvalidInputException(UserAlreadyExistsException e) {

        ResponseObject<Object> response = new ResponseObject<>(false, HttpStatus.BAD_REQUEST.value(), "Bad Request. " + e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    //handle for bad credentials
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseObject<Object>> handleBadCredentialsException(BadCredentialsException e) {

        ResponseObject<Object> response = new ResponseObject<>(false, HttpStatus.UNAUTHORIZED.value(), "Unauthorized Request. " + e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);

    }


    //all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseObject<Object>> handleGeneralException(Exception e) {

        ResponseObject<Object> response = new ResponseObject<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred: Internal Server Error");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
