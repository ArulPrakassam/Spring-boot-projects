package com.employeemanagement.config;

import com.employeemanagement.service.CustomUserDetailsService;
import com.employeemanagement.service.JwtService;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        // Skip token validation for login and register routes
        if (isPublicRoute(request)) {
            filterChain.doFilter(request, response);  // Skip token validation
            return;
        }

        String header = request.getHeader("Authorization");
        String token = null;
        String userName = null;

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);

        }

        if (token == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 Unauthorized
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"status\": 401, \"message\": \"No authentication token specified in x-auth-token header\"}");
            return;  // Return response and don't proceed further
        }

        try {
            userName = jwtService.extractUsername(token);
            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = context.getBean(CustomUserDetailsService.class).loadUserByUsername(userName);

                if (jwtService.validateToken(token, jwtService.extractUsername(token))) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 Unauthorized
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\": false, \"status\": 401, \"message\": \"Access token is not valid or has expired, you will need to login\"}");
                    return;  // Return response and don't proceed further
                }
            }
        } catch (MalformedJwtException | SignatureException | ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 Unauthorized
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"status\": 401, \"message\": \"Access token is not valid or has expired, you will need to login\"}");
            return;  // Return response and don't proceed further
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicRoute(HttpServletRequest request) {
        // Skip token validation for specific public routes
        String uri = request.getRequestURI();
        return uri.equals("/") ||
                uri.equals("/index.html") ||
                uri.equals("/swagger.html") ||
                uri.equals("/swagger.yaml") ||
                uri.equals("/h2-console/") ||
                uri.equals("/auth/login") ||
                uri.equals("/auth/register");
    }
}
