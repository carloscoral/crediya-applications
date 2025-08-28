package com.carloscoral.model.loanstatus.gateways;

import com.carloscoral.model.loanstatus.LoanStatus;

import reactor.core.publisher.Mono;

public interface LoanStatusRepository {
    Mono<LoanStatus> findByName(String name);
}
