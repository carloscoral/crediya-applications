package com.carloscoral.api;

import com.carloscoral.api.exception.ValidationException;
import com.carloscoral.model.loanstatus.LoanStatus;
import com.carloscoral.model.loanstatus.LoanStatusEnum;
import com.carloscoral.model.loanstatus.gateways.LoanStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanStatusService {

    private final LoanStatusRepository loanStatusRepository;

    public Mono<LoanStatus> validateLoanStatusExists(String loanStatusName) {
        return loanStatusRepository.findByName(loanStatusName)
                .switchIfEmpty(Mono.error(new ValidationException("Loan status not found", 
                        List.of("loanStatusName: Loan status with name '" + loanStatusName + "' does not exist"))))
                .doOnNext(loanStatus -> log.debug("Validated loan status exists: {}", loanStatus.getName()));
    }

    public Mono<LoanStatus> validateLoanStatusExists(LoanStatusEnum loanStatusEnum) {
        return validateLoanStatusExists(loanStatusEnum.getDisplayName());
    }

    public Mono<LoanStatus> getPendingReviewStatus() {
        return validateLoanStatusExists(LoanStatusEnum.PENDING_REVIEW);
    }
}
