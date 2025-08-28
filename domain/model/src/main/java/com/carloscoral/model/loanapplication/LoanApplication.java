package com.carloscoral.model.loanapplication;

import java.math.BigDecimal;
import java.util.UUID;

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
    private BigDecimal amount;
    private Integer monthsTerm;
    private String email;
    private UUID loanTypeId;
    private UUID loanStatusId;
}
