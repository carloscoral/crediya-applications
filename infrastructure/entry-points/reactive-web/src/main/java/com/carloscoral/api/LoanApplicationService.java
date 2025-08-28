package com.carloscoral.api;

import com.carloscoral.api.dto.CreateLoanApplicationRequest;
import com.carloscoral.api.exception.ValidationException;
import com.carloscoral.api.mapper.LoanApplicationMapper;
import com.carloscoral.api.validation.GenericValidator;
import com.carloscoral.model.loanapplication.gateways.LoanApplicationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanApplicationService {
    
    private final LoanApplicationMapper loanApplicationMapper;
    private final LoanApplicationRepository loanApplicationRepository;
    private final GenericValidator validator;
    private final LoanTypeService loanTypeService;
    private final LoanStatusService loanStatusService;

    public Mono<String> createLoanApplication(CreateLoanApplicationRequest request) {
        if (request == null) {
            return Mono.error(new ValidationException("Request body is required", 
                    List.of("request: Request body cannot be empty")));
        }

        return Mono.just(request)
                .doOnNext(dto -> log.debug("Processing create loan application request: {}", dto))
                .flatMap(validator::validate)
                .flatMap(validatedRequest -> 
                    Mono.zip(
                        loanTypeService.validateLoanTypeExists(validatedRequest.getLoanTypeId()),
                        loanStatusService.getPendingReviewStatus()
                    )
                    .map(tuple -> loanApplicationMapper.toLoanApplication(
                        validatedRequest, 
                        tuple.getT1(),
                        tuple.getT2()
                    ))
                )
                .flatMap(loanApplicationRepository::save)
                .doOnNext(loanApplication -> log.info("Loan application created: {}", loanApplication))
                .then(Mono.just("Loan application created successfully"));
    }
}
