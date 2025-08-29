package com.carloscoral.model.loantype.gateways;

import com.carloscoral.model.loantype.LoanType;
import reactor.core.publisher.Mono;

public interface LoanTypeRepository {
    Mono<LoanType> findById(String id);
}
