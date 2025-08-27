package com.carloscoral.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("loan_application")
public class LoanApplicationEntity {
    @Id
    private String id;

    private BigDecimal amount;

    @Column("months_term")
    private Integer monthsTerm;

    private String email;

    @Column("loan_type_id")
    private String loanTypeId;

    @Column("loan_status_id")
    private String loanStatusId;
}
