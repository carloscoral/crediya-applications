package com.carloscoral.r2dbc;

import com.carloscoral.r2dbc.entity.LoanStatusEntity;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface LoanStatusReactiveRepository extends ReactiveCrudRepository<LoanStatusEntity, String>, ReactiveQueryByExampleExecutor<LoanStatusEntity> {

}
