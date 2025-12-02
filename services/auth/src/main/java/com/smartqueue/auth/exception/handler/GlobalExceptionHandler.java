package com.smartqueue.auth.exception.handler;

import com.smartqueue.auth.dto.response.ErrorResponse;
import com.smartqueue.auth.dto.response.MethodArgumentNotValidResponse;
import com.smartqueue.auth.dto.response.SimpleErrorResponse;
import com.smartqueue.auth.exception.AuthLogInException;
import com.smartqueue.auth.exception.RoleNotFoundException;
import com.smartqueue.auth.exception.UsernameTakenException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthLogInException.class)
    public ResponseEntity<ErrorResponse> authLogIn(AuthLogInException ex,
                                                   HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(ex.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .path(request.getServletPath())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(errorResponse.getCode()).body(errorResponse);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<SimpleErrorResponse> roleNotFound(RoleNotFoundException ex) {
        SimpleErrorResponse errorResponse = SimpleErrorResponse.builder()
                .error(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(UsernameTakenException.class)
    public ResponseEntity<SimpleErrorResponse> usernameTaken(UsernameTakenException ex) {
        SimpleErrorResponse errorResponse = SimpleErrorResponse.builder()
                .error(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MethodArgumentNotValidResponse> validationArgument(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(new MethodArgumentNotValidResponse(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<SimpleErrorResponse> methodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        SimpleErrorResponse response = new SimpleErrorResponse(ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.badRequest().body(response);
    }

}
