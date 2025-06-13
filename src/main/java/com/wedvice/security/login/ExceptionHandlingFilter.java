package com.wedvice.security.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedvice.common.ApiResponse;
import com.wedvice.common.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ExceptionHandlingFilter extends OncePerRequestFilter {
    ObjectMapper a = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CustomException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            ApiResponse<?> body = ApiResponse.error(ex.httpStatus.value(), ex.getMessage());
            ObjectMapper a = new ObjectMapper();
            response.getWriter().write(a.writeValueAsString(body));
        }
    }
}
