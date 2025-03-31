package com.employeemanagement.controller;

import com.employeemanagement.dto.LoginRequest;
import com.employeemanagement.dto.ResponseObject;
import com.employeemanagement.exceptionhandling.UserAlreadyExistsException;
import com.employeemanagement.model.User;
import com.employeemanagement.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Validated
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // Register
    @PostMapping("/register")
    public ResponseEntity<ResponseObject<Object>> register(@Valid @RequestBody User registerUser) throws UserAlreadyExistsException {
        User user = authService.registerUser(registerUser);
        ResponseObject<Object> response = new ResponseObject<>(true, 201, "User Created Successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<ResponseObject<Map<String, String>>> login(@Valid @RequestBody LoginRequest loginRequest) {
        Map<String, String> token = authService.loginUser(loginRequest);
        ResponseObject<Map<String, String>> response = new ResponseObject<>(true, 200, "Successful Request", token);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

 
}
