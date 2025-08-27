package com.carloscoral.model.loantype;
import lombok.Builder;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanType {
    private String name;
    private String minAmount;
    private String maxAmount;
    private BigDecimal interestRate;
    private Boolean automaticValidation;
}
