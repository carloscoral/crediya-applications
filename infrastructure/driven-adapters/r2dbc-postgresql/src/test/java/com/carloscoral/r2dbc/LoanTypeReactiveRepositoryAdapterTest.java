package com.carloscoral.r2dbc;

import com.carloscoral.model.loantype.LoanType;
import com.carloscoral.r2dbc.entity.LoanTypeEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoanTypeReactiveRepositoryAdapter Tests")
class LoanTypeReactiveRepositoryAdapterTest {

    @Mock
    private LoanTypeReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    private LoanTypeReactiveRepositoryAdapter adapter;
    private UUID testId;
    private LoanType testLoanType;
    private LoanTypeEntity testLoanTypeEntity;

    @BeforeEach
    void setUp() {
        adapter = new LoanTypeReactiveRepositoryAdapter(repository, mapper);
        testId = UUID.randomUUID();
        
        testLoanType = LoanType.builder()
                .id(testId)
                .name("Personal Loan")
                .minAmount(new BigDecimal("1000.00"))
                .maxAmount(new BigDecimal("50000.00"))
                .interestRate(new BigDecimal("12.5"))
                .automaticValidation(true)
                .build();

        testLoanTypeEntity = LoanTypeEntity.builder()
                .id(testId)
                .name("Personal Loan")
                .minAmount(new BigDecimal("1000.00"))
                .maxAmount(new BigDecimal("50000.00"))
                .interestRate(new BigDecimal("12.5"))
                .automaticValidation(true)
                .build();
    }

    @Test
    @DisplayName("Should find loan type by UUID string successfully")
    void shouldFindLoanTypeByUuidStringSuccessfully() {
        String idString = testId.toString();
        when(repository.findById(testId)).thenReturn(Mono.just(testLoanTypeEntity));
        when(mapper.mapBuilder(eq(testLoanTypeEntity), eq(LoanType.LoanTypeBuilder.class)))
                .thenReturn(testLoanType.toBuilder());

        StepVerifier.create(adapter.findById(idString))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(testLoanType.getId(), result.getId());
                    assertEquals(testLoanType.getName(), result.getName());
                    assertEquals(testLoanType.getMinAmount(), result.getMinAmount());
                    assertEquals(testLoanType.getMaxAmount(), result.getMaxAmount());
                    assertEquals(testLoanType.getInterestRate(), result.getInterestRate());
                    assertEquals(testLoanType.getAutomaticValidation(), result.getAutomaticValidation());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty when loan type not found by UUID string")
    void shouldReturnEmptyWhenLoanTypeNotFoundByUuidString() {
        String idString = testId.toString();
        when(repository.findById(testId)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById(idString))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty when UUID string is invalid")
    void shouldReturnEmptyWhenUuidStringIsInvalid() {
        String invalidIdString = "invalid-uuid-string";

        StepVerifier.create(adapter.findById(invalidIdString))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty when UUID string is null")
    void shouldReturnEmptyWhenUuidStringIsNull() {
        String nullIdString = null;

        StepVerifier.create(adapter.findById(nullIdString))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty when UUID string is empty")
    void shouldReturnEmptyWhenUuidStringIsEmpty() {
        String emptyIdString = "";

        StepVerifier.create(adapter.findById(emptyIdString))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty when UUID string is whitespace only")
    void shouldReturnEmptyWhenUuidStringIsWhitespaceOnly() {
        String whitespaceIdString = "   ";

        StepVerifier.create(adapter.findById(whitespaceIdString))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle repository error gracefully")
    void shouldHandleRepositoryErrorGracefully() {
        String idString = testId.toString();
        when(repository.findById(testId)).thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(adapter.findById(idString))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Should inherit ReactiveAdapterOperations methods")
    void shouldInheritReactiveAdapterOperationsMethods() {
        when(repository.findById(testId)).thenReturn(Mono.just(testLoanTypeEntity));
        when(mapper.mapBuilder(eq(testLoanTypeEntity), eq(LoanType.LoanTypeBuilder.class)))
                .thenReturn(testLoanType.toBuilder());

        StepVerifier.create(adapter.findById(testId))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(testLoanType.getId(), result.getId());
                    assertEquals(testLoanType.getName(), result.getName());
                })
                .verifyComplete();
    }
}
