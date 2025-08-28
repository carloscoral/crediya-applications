package com.carloscoral.usecase.createloanapplication;

import com.carloscoral.exception.IllegalLoanStatusException;
import com.carloscoral.exception.IllegalLoanTypeException;
import com.carloscoral.exception.UserNotFoundException;
import com.carloscoral.model.loanapplication.LoanApplication;
import com.carloscoral.model.loanapplication.gateways.LoanApplicationRepository;
import com.carloscoral.model.loanstatus.LoanStatus;
import com.carloscoral.model.loanstatus.LoanStatusEnum;
import com.carloscoral.model.loanstatus.gateways.LoanStatusRepository;
import com.carloscoral.model.loantype.LoanType;
import com.carloscoral.model.loantype.gateways.LoanTypeRepository;
import com.carloscoral.model.user.gateways.ValidationUserGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateLoanApplicationUseCase Tests")
class CreateLoanApplicationUseCaseTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @Mock
    private LoanStatusRepository loanStatusRepository;

    @Mock
    private ValidationUserGateway validationUserGateway;

    private CreateLoanApplicationUseCase useCase;

    private LoanApplication testLoanApplication;
    private LoanType testLoanType;
    private LoanStatus testLoanStatus;

    @BeforeEach
    void setUp() {
        useCase = new CreateLoanApplicationUseCase(
                loanApplicationRepository,
                loanTypeRepository,
                loanStatusRepository,
                validationUserGateway
        );

        testLoanApplication = LoanApplication.builder()
                .amount(new BigDecimal("10000.00"))
                .monthsTerm(12)
                .email("test@example.com")
                .loanTypeId(UUID.randomUUID())
                .build();

        testLoanType = LoanType.builder()
                .id(testLoanApplication.getLoanTypeId())
                .name("Personal Loan")
                .build();

        testLoanStatus = LoanStatus.builder()
                .id(UUID.randomUUID())
                .name(LoanStatusEnum.PENDING_REVIEW.getDisplayName())
                .build();
    }

    @Test
    @DisplayName("Should execute successfully with valid data")
    void shouldExecuteSuccessfully() {
        LoanApplication expectedSavedApplication = testLoanApplication.toBuilder()
                .loanStatusId(testLoanStatus.getId())
                .build();

        when(validationUserGateway.validateByEmail(testLoanApplication.getEmail()))
                .thenReturn(Mono.just(true));
        
        when(loanTypeRepository.findById(testLoanApplication.getLoanTypeId().toString()))
                .thenReturn(Mono.just(testLoanType));
        
        when(loanStatusRepository.findByName(LoanStatusEnum.PENDING_REVIEW.getDisplayName()))
                .thenReturn(Mono.just(testLoanStatus));
        
        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenReturn(Mono.just(expectedSavedApplication));

        StepVerifier.create(useCase.execute(testLoanApplication))
                .expectNext(expectedSavedApplication)
                .verifyComplete();

        verify(validationUserGateway).validateByEmail(testLoanApplication.getEmail());
        verify(loanTypeRepository).findById(testLoanApplication.getLoanTypeId().toString());
        verify(loanStatusRepository).findByName(LoanStatusEnum.PENDING_REVIEW.getDisplayName());
        verify(loanApplicationRepository).save(any(LoanApplication.class));
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user validation returns false")
    void shouldThrowUserNotFoundExceptionWhenUserValidationReturnsFalse() {
        when(validationUserGateway.validateByEmail(testLoanApplication.getEmail()))
                .thenReturn(Mono.just(false));

        StepVerifier.create(useCase.execute(testLoanApplication))
                .expectErrorMatches(error -> 
                    error instanceof UserNotFoundException &&
                    error.getMessage().contains("User not found with email: " + testLoanApplication.getEmail())
                )
                .verify();

        verify(validationUserGateway).validateByEmail(testLoanApplication.getEmail());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user validation returns empty")
    void shouldThrowUserNotFoundExceptionWhenUserValidationReturnsEmpty() {
        when(validationUserGateway.validateByEmail(testLoanApplication.getEmail()))
                .thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(testLoanApplication))
                .expectErrorMatches(error -> 
                    error instanceof UserNotFoundException &&
                    error.getMessage().contains("Unable to validate user with email: " + testLoanApplication.getEmail())
                )
                .verify();

        verify(validationUserGateway).validateByEmail(testLoanApplication.getEmail());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user validation throws error")
    void shouldThrowUserNotFoundExceptionWhenUserValidationThrowsError() {
        when(validationUserGateway.validateByEmail(testLoanApplication.getEmail()))
                .thenReturn(Mono.error(new RuntimeException("Service unavailable")));

        StepVerifier.create(useCase.execute(testLoanApplication))
                .expectError(RuntimeException.class)
                .verify();

        verify(validationUserGateway).validateByEmail(testLoanApplication.getEmail());
    }

    @Test
    @DisplayName("Should throw IllegalLoanTypeException when loan type not found")
    void shouldThrowIllegalLoanTypeExceptionWhenLoanTypeNotFound() {
        when(validationUserGateway.validateByEmail(testLoanApplication.getEmail()))
                .thenReturn(Mono.just(true));
        
        when(loanTypeRepository.findById(testLoanApplication.getLoanTypeId().toString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(testLoanApplication))
                .expectErrorMatches(error -> 
                    error instanceof IllegalLoanTypeException &&
                    error.getMessage().contains("Loan type not found with id: " + testLoanApplication.getLoanTypeId())
                )
                .verify();

        verify(validationUserGateway).validateByEmail(testLoanApplication.getEmail());
        verify(loanTypeRepository).findById(testLoanApplication.getLoanTypeId().toString());
    }

    @Test
    @DisplayName("Should throw IllegalLoanStatusException when pending status not found")
    void shouldThrowIllegalLoanStatusExceptionWhenPendingStatusNotFound() {
        when(validationUserGateway.validateByEmail(testLoanApplication.getEmail()))
                .thenReturn(Mono.just(true));
        
        when(loanTypeRepository.findById(testLoanApplication.getLoanTypeId().toString()))
                .thenReturn(Mono.just(testLoanType));
        
        when(loanStatusRepository.findByName(LoanStatusEnum.PENDING_REVIEW.getDisplayName()))
                .thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(testLoanApplication))
                .expectErrorMatches(error -> 
                    error instanceof IllegalLoanStatusException &&
                    error.getMessage().contains(LoanStatusEnum.PENDING_REVIEW.getDisplayName() + " not found in database")
                )
                .verify();

        verify(validationUserGateway).validateByEmail(testLoanApplication.getEmail());
        verify(loanTypeRepository).findById(testLoanApplication.getLoanTypeId().toString());
        verify(loanStatusRepository).findByName(LoanStatusEnum.PENDING_REVIEW.getDisplayName());
    }

    @Test
    @DisplayName("Should propagate repository save error")
    void shouldPropagateRepositorySaveError() {
        RuntimeException repositoryError = new RuntimeException("Database connection failed");
        
        when(validationUserGateway.validateByEmail(testLoanApplication.getEmail()))
                .thenReturn(Mono.just(true));
        
        when(loanTypeRepository.findById(testLoanApplication.getLoanTypeId().toString()))
                .thenReturn(Mono.just(testLoanType));
        
        when(loanStatusRepository.findByName(LoanStatusEnum.PENDING_REVIEW.getDisplayName()))
                .thenReturn(Mono.just(testLoanStatus));
        
        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenReturn(Mono.error(repositoryError));

        StepVerifier.create(useCase.execute(testLoanApplication))
                .expectError(RuntimeException.class)
                .verify();

        verify(loanApplicationRepository).save(any(LoanApplication.class));
    }

    @Test
    @DisplayName("Should assign correct loan status ID to loan application")
    void shouldAssignCorrectLoanStatusIdToLoanApplication() {
        LoanApplication expectedApplication = testLoanApplication.toBuilder()
                .loanStatusId(testLoanStatus.getId())
                .build();

        when(validationUserGateway.validateByEmail(testLoanApplication.getEmail()))
                .thenReturn(Mono.just(true));
        
        when(loanTypeRepository.findById(testLoanApplication.getLoanTypeId().toString()))
                .thenReturn(Mono.just(testLoanType));
        
        when(loanStatusRepository.findByName(LoanStatusEnum.PENDING_REVIEW.getDisplayName()))
                .thenReturn(Mono.just(testLoanStatus));
        
        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenReturn(Mono.just(expectedApplication));

        StepVerifier.create(useCase.execute(testLoanApplication))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(testLoanStatus.getId(), result.getLoanStatusId());
                    assertEquals(testLoanApplication.getEmail(), result.getEmail());
                    assertEquals(testLoanApplication.getAmount(), result.getAmount());
                    assertEquals(testLoanApplication.getMonthsTerm(), result.getMonthsTerm());
                })
                .verifyComplete();
    }
}
