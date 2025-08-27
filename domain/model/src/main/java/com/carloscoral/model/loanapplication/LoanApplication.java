package com.carloscoral.model.loanapplication;
import com.carloscoral.model.loantype.LoanType;
import com.carloscoral.model.loanstatus.LoanStatus;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplication {
    private String amount;
    private Integer monthsTerm;
    private String email;
    private LoanType loanType;
    private LoanStatus loanStatus;
}
