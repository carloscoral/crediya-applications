package com.carloscoral.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body for creating a new loan application")
public class CreateLoanApplicationRequest {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Digits(integer = 13, fraction = 2, message = "Amount must have at most 13 digits and 2 decimal places")
    @Schema(description = "Loan amount requested", 
            example = "15000.00", 
            minimum = "0.01")
    private BigDecimal amount;

    @NotNull(message = "Months term is required")
    @Min(value = 1, message = "Months term must be at least 1 month")
    @Schema(description = "Loan term in months", 
            example = "24", 
            minimum = "1", 
            maximum = "360")
    private Integer monthsTerm;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    @Schema(description = "Applicant's email address", 
            example = "carlos.coral@example.com")
    private String email;

    @NotBlank(message = "Loan type ID is required")
    @org.hibernate.validator.constraints.UUID(message = "Loan type ID must be a valid UUID")
    @Schema(description = "UUID of the loan type being requested", 
            example = "123e4567-e89b-12d3-a456-426614174000")
    private String loanTypeId;
}
