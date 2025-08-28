package com.carloscoral.usecase.createloanapplication;

import com.carloscoral.model.loanapplication.LoanApplication;
import com.carloscoral.model.loanapplication.gateways.LoanApplicationRepository;
import com.carloscoral.model.loantype.gateways.LoanTypeRepository;
import com.carloscoral.model.loanstatus.gateways.LoanStatusRepository;
import com.carloscoral.model.loanstatus.LoanStatusEnum;
import com.carloscoral.exception.IllegalLoanTypeException;
import com.carloscoral.exception.IllegalLoanStatusException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateLoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final LoanStatusRepository loanStatusRepository;

    public Mono<LoanApplication> execute(LoanApplication loanApplication) {
        return validateLoanType(loanApplication)
                .flatMap(this::assignPendingStatus)
                .flatMap(loanApplicationRepository::save);
    }

    private Mono<LoanApplication> validateLoanType(LoanApplication loanApplication) {
        return loanTypeRepository.findById(loanApplication.getLoanTypeId().toString())
                .switchIfEmpty(Mono.error(new IllegalLoanTypeException("Loan type not found with id: " + loanApplication.getLoanTypeId())))
                .then(Mono.just(loanApplication));
    }

    private Mono<LoanApplication> assignPendingStatus(LoanApplication loanApplication) {
        return loanStatusRepository.findByName(LoanStatusEnum.PENDING_REVIEW.getDisplayName())
                .switchIfEmpty(Mono.error(new IllegalLoanStatusException(String.format("%s not found in database", LoanStatusEnum.PENDING_REVIEW.getDisplayName()))))
                .map(loanStatus -> loanApplication.toBuilder()
                        .loanStatusId(loanStatus.getId())
                        .build());
    }
}
