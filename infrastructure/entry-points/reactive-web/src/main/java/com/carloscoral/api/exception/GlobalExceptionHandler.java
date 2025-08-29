package com.carloscoral.api.exception;

import com.carloscoral.api.dto.ApiResponse;
import com.carloscoral.exception.IllegalLoanStatusException;
import com.carloscoral.exception.IllegalLoanTypeException;
import com.carloscoral.exception.UserNotFoundException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public Mono<ResponseEntity<ApiResponse<List<String>>>> handleValidationException(ValidationException validationException) {
        log.warn("Validation error: {}", validationException.getValidationErrors());
        
        ApiResponse<List<String>> errorResponse = ApiResponse.error("Validation failed", validationException.getValidationErrors());
        
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse));
    }

    @ExceptionHandler({DecodingException.class, JsonParseException.class, JsonProcessingException.class})
    public Mono<ResponseEntity<ApiResponse<Object>>> handleJsonParsingException(Exception exception) {
        log.warn("Invalid JSON format: {}", exception.getMessage());
        
        ApiResponse<Object> errorResponse = ApiResponse.error("Invalid JSON format in request body");
        
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse));
    }

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<ApiResponse<Object>>> handleServerWebInputException(ServerWebInputException exception) {
        log.warn("Invalid request input: {}", exception.getMessage());
        
        Throwable cause = exception.getCause();
        while (cause != null) {
            if (cause instanceof JsonProcessingException) {
                ApiResponse<Object> errorResponse = ApiResponse.error("Invalid JSON format in request body");
                return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorResponse));
            }
            cause = cause.getCause();
        }
        
        ApiResponse<Object> errorResponse = ApiResponse.error("Invalid request format");
        
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiResponse<Object>>> handleGenericException(Exception error) {
        log.error("Unexpected error: {}", error.getMessage(), error);
        
        ApiResponse<Object> errorResponse = ApiResponse.error("Internal server error occurred");
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse));
    }

    @ExceptionHandler(IllegalLoanTypeException.class)
    public Mono<ResponseEntity<ApiResponse<Object>>> handleIllegalLoanTypeException(IllegalLoanTypeException exception) {
        log.warn("Illegal loan type exception: {}", exception.getMessage());
        
        ApiResponse<Object> errorResponse = ApiResponse.error(exception.getMessage());
        
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse));
    }

    @ExceptionHandler(IllegalLoanStatusException.class)
    public Mono<ResponseEntity<ApiResponse<Object>>> handleIllegalLoanStatusException(IllegalLoanStatusException exception) {
        log.warn("Illegal loan status exception: {}", exception.getMessage());
        
        ApiResponse<Object> errorResponse = ApiResponse.error(exception.getMessage());
        
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<ApiResponse<Object>>> handleUserNotFoundException(UserNotFoundException exception) {
        log.warn("User not found exception: {}", exception.getMessage());
        
        ApiResponse<Object> errorResponse = ApiResponse.error(exception.getMessage());
        
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse));
    }
}
