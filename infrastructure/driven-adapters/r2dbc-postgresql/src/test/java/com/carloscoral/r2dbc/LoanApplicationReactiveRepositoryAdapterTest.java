package com.carloscoral.r2dbc;

import com.carloscoral.model.loanapplication.LoanApplication;
import com.carloscoral.r2dbc.entity.LoanApplicationEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanApplicationReactiveRepositoryAdapterTest {

    @InjectMocks
    LoanApplicationReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    LoanApplicationReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    private LoanApplicationEntity createTestEntity() {
        return LoanApplicationEntity.builder()
                .id("1")
                .amount(new BigDecimal("10000"))
                .monthsTerm(12)
                .email("test@test.com")
                .build();
    }

    private LoanApplication createTestDomain() {
        return LoanApplication.builder()
                .amount(new BigDecimal("10000"))
                .monthsTerm(12)
                .email("test@test.com")
                .build();
    }

    @Test
    void mustFindValueById() {
        LoanApplicationEntity entity = createTestEntity();
        LoanApplication domain = createTestDomain();

        when(repository.findById("1")).thenReturn(Mono.just(entity));
        when(mapper.mapBuilder(eq(entity), eq(LoanApplication.LoanApplicationBuilder.class)))
                .thenReturn(domain.toBuilder());

        Mono<LoanApplication> result = repositoryAdapter.findById("1");

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getAmount().equals(new BigDecimal("10000")) && value.getEmail().equals("test@test.com"))
                .verifyComplete();
    }

    @Test
    void mustFindAllValues() {
        LoanApplicationEntity entity = createTestEntity();
        LoanApplication domain = createTestDomain();

        when(repository.findAll()).thenReturn(Flux.just(entity));
        when(mapper.mapBuilder(eq(entity), eq(LoanApplication.LoanApplicationBuilder.class)))
                .thenReturn(domain.toBuilder());

        Flux<LoanApplication> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getAmount().equals(new BigDecimal("10000")) && value.getEmail().equals("test@test.com"))
                .verifyComplete();
    }

    @Test
    @SuppressWarnings("unchecked")
    void mustFindByExample() {
        LoanApplicationEntity entity = createTestEntity();
        LoanApplication domain = createTestDomain();
        LoanApplication example = createTestDomain();
        
        when(mapper.map(eq(example), eq(LoanApplicationEntity.class))).thenReturn(entity);
        when(repository.findAll(any(Example.class))).thenReturn(Flux.just(entity));
        when(mapper.mapBuilder(eq(entity), eq(LoanApplication.LoanApplicationBuilder.class)))
                .thenReturn(domain.toBuilder());

        Flux<LoanApplication> result = repositoryAdapter.findByExample(example);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getAmount().equals(new BigDecimal("10000")) && value.getEmail().equals("test@test.com"))
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        LoanApplicationEntity entity = createTestEntity();
        LoanApplication domain = createTestDomain();

        when(mapper.map(eq(domain), eq(LoanApplicationEntity.class))).thenReturn(entity);
        when(repository.save(eq(entity))).thenReturn(Mono.just(entity));
        when(mapper.mapBuilder(eq(entity), eq(LoanApplication.LoanApplicationBuilder.class)))
                .thenReturn(domain.toBuilder());

        Mono<LoanApplication> result = repositoryAdapter.save(domain);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getAmount().equals(new BigDecimal("10000")) && value.getEmail().equals("test@test.com"))
                .verifyComplete();
    }
}
