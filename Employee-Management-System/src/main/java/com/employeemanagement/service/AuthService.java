package com.employeemanagement.service;


import com.employeemanagement.dto.LoginRequest;
import com.employeemanagement.exceptionhandling.UserAlreadyExistsException;
import com.employeemanagement.model.User;
import com.employeemanagement.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public User registerUser(User user) throws UserAlreadyExistsException {
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with this email already exists.");

        }


        user.setPassword(encoder.encode(user.getPassword())); // Encrypt password
        user.setRole("ROLE_" + user.getRole());

        return userRepo.save(user);
    }

    public Map<String, String> loginUser(LoginRequest loginRequest) {
        Map<String, String> tokenValue = new HashMap<>();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        // After successful authentication, generate the JWT token
        String token = jwtService.generateToken(authentication.getName(), authentication.getAuthorities().toString());

        tokenValue.put("token", token);
        return tokenValue;
    }
}

