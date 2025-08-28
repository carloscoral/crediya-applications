package com.carloscoral.api;

import com.carloscoral.api.exception.ValidationException;
import com.carloscoral.model.loantype.LoanType;
import com.carloscoral.model.loantype.gateways.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanTypeService {

    private final LoanTypeRepository loanTypeRepository;

    public Mono<LoanType> validateLoanTypeExists(String loanTypeId) {
        log.info("Validating loan type exists: {}", loanTypeId);
        return loanTypeRepository.findById(loanTypeId)
                .switchIfEmpty(Mono.error(new ValidationException("Loan type not found", 
                        List.of("loanTypeId: Loan type with ID '" + loanTypeId + "' does not exist"))))
                .doOnNext(loanType -> log.debug("Validated loan type exists: {}", loanType.getName()));
    }
}
