package com.smartqueue.auth.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartqueue.auth.dto.response.ErrorResponse;
import com.smartqueue.auth.exception.InvalidTokenException;
import com.smartqueue.auth.security.TokenAuthentication;
import com.smartqueue.auth.security.service.JwtService;
import com.smartqueue.auth.util.TokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = TokenUtils.parseToken(request);

            if (token != null) {
                if (jwtService.isTokenExpired(token)) {
                    throw new InvalidTokenException();
                }
                TokenAuthentication authentication = new TokenAuthentication(jwtService.parseToken(token));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (InvalidTokenException ex) {
            sendErrorResponse(request, response, ex);
        }
    }

    private void sendErrorResponse(HttpServletRequest request,
                                   HttpServletResponse response,
                                   InvalidTokenException ex)
            throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .path(request.getServletPath())
                .error(ex.getMessage())
                .code(HttpStatus.UNAUTHORIZED.value())
                .timestamp(LocalDateTime.now())
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

}
