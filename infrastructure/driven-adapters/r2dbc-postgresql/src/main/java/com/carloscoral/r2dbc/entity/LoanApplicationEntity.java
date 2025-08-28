package com.carloscoral.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("loan_application")
public class LoanApplicationEntity {
    @Id
    private UUID id;

    private BigDecimal amount;

    @Column("months_term")
    private Integer monthsTerm;

    private String email;

    @Column("loan_type_id")
    private UUID loanTypeId;

    @Column("loan_status_id")
    private UUID loanStatusId;
}
