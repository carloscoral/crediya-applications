package com.carloscoral.model.loanapplication.gateways;

import com.carloscoral.model.loanapplication.LoanApplication;

import reactor.core.publisher.Mono;

public interface LoanApplicationRepository {
    Mono<LoanApplication> save(LoanApplication loanApplication);
}
