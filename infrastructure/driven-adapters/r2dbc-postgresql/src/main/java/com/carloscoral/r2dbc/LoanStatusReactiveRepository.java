package com.carloscoral.r2dbc;

import com.carloscoral.r2dbc.entity.LoanStatusEntity;

import reactor.core.publisher.Mono;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface LoanStatusReactiveRepository extends ReactiveCrudRepository<LoanStatusEntity, UUID>, ReactiveQueryByExampleExecutor<LoanStatusEntity> {
    Mono<LoanStatusEntity> findByName(String name);
}
