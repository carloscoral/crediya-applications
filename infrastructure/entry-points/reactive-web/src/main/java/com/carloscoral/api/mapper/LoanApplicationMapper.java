package com.carloscoral.api.mapper;

import com.carloscoral.api.dto.CreateLoanApplicationRequest;
import com.carloscoral.model.loanapplication.LoanApplication;
import com.carloscoral.model.loanstatus.LoanStatus;
import com.carloscoral.model.loantype.LoanType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoanApplicationMapper {

    public LoanApplication toLoanApplication(CreateLoanApplicationRequest request, LoanType loanType, LoanStatus loanStatus) {
        return LoanApplication.builder()
                .amount(request.getAmount())
                .monthsTerm(request.getMonthsTerm())
                .email(request.getEmail())
                .loanType(loanType)
                .loanStatus(loanStatus)
                .build();
    }
}

