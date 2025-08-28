package com.carloscoral.api.mapper;

import com.carloscoral.api.dto.CreateLoanApplicationRequest;
import com.carloscoral.model.loanapplication.LoanApplication;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LoanApplicationMapper {
    
    @Mapping(target = "loanTypeId", expression = "java(java.util.UUID.fromString(request.getLoanTypeId()))")
    @Mapping(target = "loanStatusId", ignore = true)
    LoanApplication toLoanApplication(CreateLoanApplicationRequest request);
}

