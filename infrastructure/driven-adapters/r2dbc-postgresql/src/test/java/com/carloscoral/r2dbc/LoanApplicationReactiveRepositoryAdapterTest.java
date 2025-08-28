package com.carloscoral.r2dbc;

import com.carloscoral.model.loanapplication.LoanApplication;
import com.carloscoral.r2dbc.entity.LoanApplicationEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
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

    @Test
    void shouldTestInheritedFindByIdMethod() {
        setupRepositoryAdapter();
        LoanApplicationEntity entity = createTestEntity();
        LoanApplication expectedDomain = createTestDomain();

        when(repository.findById(TEST_ID))
                .thenReturn(Mono.just(entity));

        when(mapper.mapBuilder(eq(entity), eq(LoanApplication.LoanApplicationBuilder.class)))
                .thenReturn(expectedDomain.toBuilder());

        StepVerifier.create(repositoryAdapter.findById(TEST_ID))
                .assertNext(result -> {
                    assertNotNull(result, "Result should not be null");
                    assertEquals(expectedDomain.getAmount(), result.getAmount(), "Amount should match");
                    assertEquals(expectedDomain.getEmail(), result.getEmail(), "Email should match");
                    assertEquals(expectedDomain.getMonthsTerm(), result.getMonthsTerm(), "MonthsTerm should match");
                    assertEquals(expectedDomain.getLoanTypeId(), result.getLoanTypeId(), "LoanTypeId should match");
                    assertEquals(expectedDomain.getLoanStatusId(), result.getLoanStatusId(), "LoanStatusId should match");
                })
                .verifyComplete();
    }

    @Test
    void shouldTestInheritedFindAllMethod() {
        setupRepositoryAdapter();
        LoanApplicationEntity entity1 = createTestEntity();
        LoanApplicationEntity entity2 = LoanApplicationEntity.builder()
                .id(UUID.fromString("456e4567-e89b-12d3-a456-426614174001"))
                .amount(new BigDecimal("20000.00"))
                .monthsTerm(24)
                .email("test2@test.com")
                .loanTypeId(TEST_LOAN_TYPE_ID)
                .loanStatusId(TEST_LOAN_STATUS_ID)
                .build();

        LoanApplication domain1 = createTestDomain();
        LoanApplication domain2 = LoanApplication.builder()
                .amount(new BigDecimal("20000.00"))
                .monthsTerm(24)
                .email("test2@test.com")
                .loanTypeId(TEST_LOAN_TYPE_ID)
                .loanStatusId(TEST_LOAN_STATUS_ID)
                .build();

        when(repository.findAll())
                .thenReturn(Flux.just(entity1, entity2));

        when(mapper.mapBuilder(eq(entity1), eq(LoanApplication.LoanApplicationBuilder.class)))
                .thenReturn(domain1.toBuilder());
        when(mapper.mapBuilder(eq(entity2), eq(LoanApplication.LoanApplicationBuilder.class)))
                .thenReturn(domain2.toBuilder());

        StepVerifier.create(repositoryAdapter.findAll())
                .assertNext(result -> {
                    assertEquals(domain1.getAmount(), result.getAmount(), "First domain amount should match");
                    assertEquals(domain1.getEmail(), result.getEmail(), "First domain email should match");
                })
                .assertNext(result -> {
                    assertEquals(domain2.getAmount(), result.getAmount(), "Second domain amount should match");
                    assertEquals(domain2.getEmail(), result.getEmail(), "Second domain email should match");
                })
                .verifyComplete();
    }

    @Test
    void shouldTestInheritedFindByIdWhenNotFound() {
        setupRepositoryAdapter();
        UUID nonExistentId = UUID.fromString("999e4567-e89b-12d3-a456-426614174999");

        when(repository.findById(nonExistentId))
                .thenReturn(Mono.empty());

        StepVerifier.create(repositoryAdapter.findById(nonExistentId))
                .verifyComplete();
    }

    @Test
    void shouldHandleMapperErrorInLambda() {
        setupRepositoryAdapter();
        LoanApplicationEntity entity = createTestEntity();

        when(repository.findById(TEST_ID))
                .thenReturn(Mono.just(entity));

        when(mapper.mapBuilder(eq(entity), eq(LoanApplication.LoanApplicationBuilder.class)))
                .thenThrow(new RuntimeException("Mapping error in lambda"));

        StepVerifier.create(repositoryAdapter.findById(TEST_ID))
                .expectError(RuntimeException.class)
                .verify();
    }
}
