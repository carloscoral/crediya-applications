package com.carloscoral.api;

import com.carloscoral.api.dto.CreateLoanApplicationRequest;
import com.carloscoral.api.exception.ValidationException;
import com.carloscoral.api.mapper.LoanApplicationMapper;
import com.carloscoral.api.validation.GenericValidator;
import com.carloscoral.exception.UserNotFoundException;
import com.carloscoral.model.loanapplication.LoanApplication;
import com.carloscoral.usecase.createloanapplication.CreateLoanApplicationUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoanApplicationService Tests")
class LoanApplicationServiceTest {

    @Mock
    private LoanApplicationMapper loanApplicationMapper;

    @Mock
    private CreateLoanApplicationUseCase createLoanApplicationUseCase;

    @Mock
    private GenericValidator validator;

    private LoanApplicationService service;

    private CreateLoanApplicationRequest validRequest;
    private LoanApplication testLoanApplication;

    @BeforeEach
    void setUp() {
        service = new LoanApplicationService(
                loanApplicationMapper,
                createLoanApplicationUseCase,
                validator
        );

        validRequest = CreateLoanApplicationRequest.builder()
                .amount(new BigDecimal("10000.00"))
                .monthsTerm(12)
                .email("test@example.com")
                .loanTypeId("123e4567-e89b-12d3-a456-426614174000")
                .build();

        testLoanApplication = LoanApplication.builder()
                .amount(new BigDecimal("10000.00"))
                .monthsTerm(12)
                .email("test@example.com")
                .loanTypeId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .loanStatusId(UUID.randomUUID())
                .build();
    }

    @Test
    @DisplayName("Should create loan application successfully")
    void shouldCreateLoanApplicationSuccessfully() {
        when(validator.validate(validRequest)).thenReturn(Mono.just(validRequest));
        when(loanApplicationMapper.toLoanApplication(validRequest)).thenReturn(testLoanApplication);
        when(createLoanApplicationUseCase.execute(testLoanApplication)).thenReturn(Mono.just(testLoanApplication));

        StepVerifier.create(service.createLoanApplication(validRequest))
                .expectNext("Loan application created successfully")
                .verifyComplete();

        verify(validator).validate(validRequest);
        verify(loanApplicationMapper).toLoanApplication(validRequest);
        verify(createLoanApplicationUseCase).execute(testLoanApplication);
    }

    @Test
    @DisplayName("Should throw ValidationException when request is null")
    void shouldThrowValidationExceptionWhenRequestIsNull() {
        StepVerifier.create(service.createLoanApplication(null))
                .expectErrorMatches(error -> 
                    error instanceof ValidationException &&
                    error.getMessage().equals("Request body is required") &&
                    ((ValidationException) error).getValidationErrors().contains("request: Request body cannot be empty")
                )
                .verify();

        verifyNoInteractions(validator, loanApplicationMapper, createLoanApplicationUseCase);
    }

    @Test
    @DisplayName("Should propagate validation error from validator")
    void shouldPropagateValidationErrorFromValidator() {
        List<String> validationErrors = Arrays.asList("amount: must be positive", "email: invalid format");
        ValidationException validationException = new ValidationException("Validation failed", validationErrors);
        
        when(validator.validate(validRequest)).thenReturn(Mono.error(validationException));

        StepVerifier.create(service.createLoanApplication(validRequest))
                .expectError(ValidationException.class)
                .verify();

        verify(validator).validate(validRequest);
        verifyNoInteractions(loanApplicationMapper, createLoanApplicationUseCase);
    }

    @Test
    @DisplayName("Should propagate error from mapper")
    void shouldPropagateErrorFromMapper() {
        RuntimeException mapperException = new RuntimeException("Mapping failed");
        
        when(validator.validate(validRequest)).thenReturn(Mono.just(validRequest));
        when(loanApplicationMapper.toLoanApplication(validRequest)).thenThrow(mapperException);

        StepVerifier.create(service.createLoanApplication(validRequest))
                .expectError(RuntimeException.class)
                .verify();

        verify(validator).validate(validRequest);
        verify(loanApplicationMapper).toLoanApplication(validRequest);
        verifyNoInteractions(createLoanApplicationUseCase);
    }

    @Test
    @DisplayName("Should propagate error from use case")
    void shouldPropagateErrorFromUseCase() {
        UserNotFoundException useCaseException = new UserNotFoundException("User not found");
        
        when(validator.validate(validRequest)).thenReturn(Mono.just(validRequest));
        when(loanApplicationMapper.toLoanApplication(validRequest)).thenReturn(testLoanApplication);
        when(createLoanApplicationUseCase.execute(testLoanApplication)).thenReturn(Mono.error(useCaseException));

        StepVerifier.create(service.createLoanApplication(validRequest))
                .expectError(UserNotFoundException.class)
                .verify();

        verify(validator).validate(validRequest);
        verify(loanApplicationMapper).toLoanApplication(validRequest);
        verify(createLoanApplicationUseCase).execute(testLoanApplication);
    }

    @Test
    @DisplayName("Should handle runtime exception during validation")
    void shouldHandleRuntimeExceptionDuringValidation() {
        RuntimeException runtimeException = new RuntimeException("Unexpected error during validation");
        
        when(validator.validate(validRequest)).thenReturn(Mono.error(runtimeException));

        StepVerifier.create(service.createLoanApplication(validRequest))
                .expectError(RuntimeException.class)
                .verify();

        verify(validator).validate(validRequest);
        verifyNoInteractions(loanApplicationMapper, createLoanApplicationUseCase);
    }

    @Test
    @DisplayName("Should complete the chain even when use case returns different loan application")
    void shouldCompleteChainWithDifferentLoanApplication() {
        LoanApplication modifiedLoanApplication = testLoanApplication.toBuilder()
                .loanStatusId(UUID.randomUUID())
                .build();
        
        when(validator.validate(validRequest)).thenReturn(Mono.just(validRequest));
        when(loanApplicationMapper.toLoanApplication(validRequest)).thenReturn(testLoanApplication);
        when(createLoanApplicationUseCase.execute(testLoanApplication)).thenReturn(Mono.just(modifiedLoanApplication));

        StepVerifier.create(service.createLoanApplication(validRequest))
                .expectNext("Loan application created successfully")
                .verifyComplete();

        verify(validator).validate(validRequest);
        verify(loanApplicationMapper).toLoanApplication(validRequest);
        verify(createLoanApplicationUseCase).execute(testLoanApplication);
    }
}
