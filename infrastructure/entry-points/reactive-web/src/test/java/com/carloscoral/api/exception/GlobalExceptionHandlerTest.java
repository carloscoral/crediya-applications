package com.carloscoral.api.exception;

import com.carloscoral.api.dto.ApiResponse;
import com.carloscoral.exception.IllegalLoanStatusException;
import com.carloscoral.exception.IllegalLoanTypeException;
import com.carloscoral.exception.UserNotFoundException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebInputException;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should handle ValidationException correctly")
    void shouldHandleValidationException() {
        List<String> errors = Arrays.asList("Field is required", "Invalid format");
        ValidationException exception = new ValidationException("Validation failed", errors);

        StepVerifier.create(exceptionHandler.handleValidationException(exception))
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
                    
                    ApiResponse<List<String>> body = response.getBody();
                    assertNotNull(body);
                    assertEquals("Validation failed", body.getMessage());
                    assertFalse(body.isSuccess());
                    assertEquals(errors, body.getErrors());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle DecodingException correctly")
    void shouldHandleDecodingException() {
        DecodingException exception = new DecodingException("Invalid JSON format");

        StepVerifier.create(exceptionHandler.handleJsonParsingException(exception))
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
                    
                    ApiResponse<Object> body = response.getBody();
                    assertNotNull(body);
                    assertEquals("Invalid JSON format in request body", body.getMessage());
                    assertFalse(body.isSuccess());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle JsonParseException correctly")
    void shouldHandleJsonParseException() {
        JsonParseException exception = new JsonParseException(null, "Invalid JSON syntax");

        StepVerifier.create(exceptionHandler.handleJsonParsingException(exception))
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
                    
                    ApiResponse<Object> body = response.getBody();
                    assertNotNull(body);
                    assertEquals("Invalid JSON format in request body", body.getMessage());
                    assertFalse(body.isSuccess());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle JsonProcessingException correctly")
    void shouldHandleJsonProcessingException() {
        JsonProcessingException exception = new JsonProcessingException("Processing error") {};

        StepVerifier.create(exceptionHandler.handleJsonParsingException(exception))
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
                    
                    ApiResponse<Object> body = response.getBody();
                    assertNotNull(body);
                    assertEquals("Invalid JSON format in request body", body.getMessage());
                    assertFalse(body.isSuccess());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle ServerWebInputException with JsonProcessingException cause")
    void shouldHandleServerWebInputExceptionWithJsonProcessingCause() {
        JsonProcessingException cause = new JsonProcessingException("JSON error") {};
        ServerWebInputException exception = new ServerWebInputException("Server input error", null, cause);

        StepVerifier.create(exceptionHandler.handleServerWebInputException(exception))
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
                    
                    ApiResponse<Object> body = response.getBody();
                    assertNotNull(body);
                    assertEquals("Invalid JSON format in request body", body.getMessage());
                    assertFalse(body.isSuccess());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle ServerWebInputException without JsonProcessingException cause")
    void shouldHandleServerWebInputExceptionWithoutJsonProcessingCause() {
        RuntimeException cause = new RuntimeException("Generic error");
        ServerWebInputException exception = new ServerWebInputException("Server input error", null, cause);

        StepVerifier.create(exceptionHandler.handleServerWebInputException(exception))
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
                    
                    ApiResponse<Object> body = response.getBody();
                    assertNotNull(body);
                    assertEquals("Invalid request format", body.getMessage());
                    assertFalse(body.isSuccess());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle ServerWebInputException with null cause")
    void shouldHandleServerWebInputExceptionWithNullCause() {
        ServerWebInputException exception = new ServerWebInputException("Server input error");

        StepVerifier.create(exceptionHandler.handleServerWebInputException(exception))
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
                    
                    ApiResponse<Object> body = response.getBody();
                    assertNotNull(body);
                    assertEquals("Invalid request format", body.getMessage());
                    assertFalse(body.isSuccess());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle generic Exception correctly")
    void shouldHandleGenericException() {
        RuntimeException exception = new RuntimeException("Unexpected error");

        StepVerifier.create(exceptionHandler.handleGenericException(exception))
                .assertNext(response -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                    assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
                    
                    ApiResponse<Object> body = response.getBody();
                    assertNotNull(body);
                    assertEquals("Internal server error occurred", body.getMessage());
                    assertFalse(body.isSuccess());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle IllegalLoanTypeException correctly")
    void shouldHandleIllegalLoanTypeException() {
        IllegalLoanTypeException exception = new IllegalLoanTypeException("Loan type not found");

        StepVerifier.create(exceptionHandler.handleIllegalLoanTypeException(exception))
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
                    
                    ApiResponse<Object> body = response.getBody();
                    assertNotNull(body);
                    assertEquals("Loan type not found", body.getMessage());
                    assertFalse(body.isSuccess());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle IllegalLoanStatusException correctly")
    void shouldHandleIllegalLoanStatusException() {
        IllegalLoanStatusException exception = new IllegalLoanStatusException("Loan status not found");

        StepVerifier.create(exceptionHandler.handleIllegalLoanStatusException(exception))
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
                    
                    ApiResponse<Object> body = response.getBody();
                    assertNotNull(body);
                    assertEquals("Loan status not found", body.getMessage());
                    assertFalse(body.isSuccess());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle UserNotFoundException correctly")
    void shouldHandleUserNotFoundException() {
        UserNotFoundException exception = new UserNotFoundException("User not found");

        StepVerifier.create(exceptionHandler.handleUserNotFoundException(exception))
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
                    
                    ApiResponse<Object> body = response.getBody();
                    assertNotNull(body);
                    assertEquals("User not found", body.getMessage());
                    assertFalse(body.isSuccess());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle ServerWebInputException with nested JsonProcessingException cause")
    void shouldHandleServerWebInputExceptionWithNestedJsonProcessingCause() {
        JsonProcessingException deepCause = new JsonProcessingException("Deep JSON error") {};
        RuntimeException middleCause = new RuntimeException("Middle cause", deepCause);
        ServerWebInputException exception = new ServerWebInputException("Server input error", null, middleCause);

        StepVerifier.create(exceptionHandler.handleServerWebInputException(exception))
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
                    
                    ApiResponse<Object> body = response.getBody();
                    assertNotNull(body);
                    assertEquals("Invalid JSON format in request body", body.getMessage());
                    assertFalse(body.isSuccess());
                })
                .verifyComplete();
    }
}
