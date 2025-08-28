package com.carloscoral.r2dbc;

import com.carloscoral.model.loanapplication.LoanApplication;
import com.carloscoral.r2dbc.entity.LoanApplicationEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanApplicationReactiveRepositoryAdapterTest {

    @Mock
    LoanApplicationReactiveRepository repository;

    @Mock
    LoanTypeReactiveRepository loanTypeRepository;

    @Mock
    LoanStatusReactiveRepository loanStatusRepository;

    @Mock
    ObjectMapper mapper;

    LoanApplicationReactiveRepositoryAdapter repositoryAdapter;

    private final UUID TEST_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private final UUID TEST_LOAN_TYPE_ID = UUID.fromString("223e4567-e89b-12d3-a456-426614174000");
    private final UUID TEST_LOAN_STATUS_ID = UUID.fromString("323e4567-e89b-12d3-a456-426614174000");

    private LoanApplicationEntity createTestEntity() {
        return LoanApplicationEntity.builder()
                .id(TEST_ID)
                .amount(new BigDecimal("10000.00"))
                .monthsTerm(12)
                .email("test@test.com")
                .loanTypeId(TEST_LOAN_TYPE_ID)
                .loanStatusId(TEST_LOAN_STATUS_ID)
                .build();
    }

    private LoanApplication createTestDomain() {
        return LoanApplication.builder()
                .amount(new BigDecimal("10000.00"))
                .monthsTerm(12)
                .email("test@test.com")
                .loanTypeId(TEST_LOAN_TYPE_ID)
                .loanStatusId(TEST_LOAN_STATUS_ID)
                .build();
    }

    private void setupRepositoryAdapter() {
        repositoryAdapter = new LoanApplicationReactiveRepositoryAdapter(
                repository, mapper, loanTypeRepository, loanStatusRepository);
    }

    @Test
    void shouldSaveLoanApplication() {
        setupRepositoryAdapter();
        LoanApplication domainToSave = createTestDomain();
        LoanApplicationEntity entityToBeSaved = createTestEntity();
        LoanApplicationEntity savedEntity = createTestEntity();
        LoanApplication expectedDomain = createTestDomain();

        when(mapper.mapBuilder(eq(domainToSave), eq(LoanApplicationEntity.LoanApplicationEntityBuilder.class)))
                .thenReturn(LoanApplicationEntity.builder()
                        .id(entityToBeSaved.getId())
                        .amount(entityToBeSaved.getAmount())
                        .monthsTerm(entityToBeSaved.getMonthsTerm())
                        .email(entityToBeSaved.getEmail())
                        .loanTypeId(entityToBeSaved.getLoanTypeId())
                        .loanStatusId(entityToBeSaved.getLoanStatusId()));

        when(repository.save(any(LoanApplicationEntity.class)))
                .thenReturn(Mono.just(savedEntity));

        when(mapper.mapBuilder(eq(savedEntity), eq(LoanApplication.LoanApplicationBuilder.class)))
                .thenReturn(expectedDomain.toBuilder());

        Mono<LoanApplication> result = repositoryAdapter.save(domainToSave);

        StepVerifier.create(result)
                .expectNextMatches(loanApplication -> 
                    loanApplication.getAmount().equals(new BigDecimal("10000.00")) &&
                    loanApplication.getEmail().equals("test@test.com") &&
                    loanApplication.getMonthsTerm().equals(12) &&
                    loanApplication.getLoanTypeId().equals(TEST_LOAN_TYPE_ID) &&
                    loanApplication.getLoanStatusId().equals(TEST_LOAN_STATUS_ID)
                )
                .verifyComplete();
    }



    @Test
    void shouldPropagateRepositoryErrorOnSave() {
        setupRepositoryAdapter();
        LoanApplication domainToSave = createTestDomain();
        LoanApplicationEntity entityToBeSaved = createTestEntity();
        RuntimeException repositoryError = new RuntimeException("Database connection failed");

        when(mapper.mapBuilder(eq(domainToSave), eq(LoanApplicationEntity.LoanApplicationEntityBuilder.class)))
                .thenReturn(LoanApplicationEntity.builder()
                        .id(entityToBeSaved.getId())
                        .amount(entityToBeSaved.getAmount())
                        .monthsTerm(entityToBeSaved.getMonthsTerm())
                        .email(entityToBeSaved.getEmail())
                        .loanTypeId(entityToBeSaved.getLoanTypeId())
                        .loanStatusId(entityToBeSaved.getLoanStatusId()));

        when(repository.save(any(LoanApplicationEntity.class)))
                .thenReturn(Mono.error(repositoryError));


        StepVerifier.create(repositoryAdapter.save(domainToSave))
                .expectError(RuntimeException.class)
                .verify();
    }
}
