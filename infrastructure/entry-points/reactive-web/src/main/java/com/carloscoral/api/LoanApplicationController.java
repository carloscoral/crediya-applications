package com.carloscoral.api;

import com.carloscoral.api.dto.ApiResponse;
import com.carloscoral.api.dto.CreateLoanApplicationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class LoanApplicationController {

    @PostMapping(path = "/loan-applications")
    @Operation(
            summary = "Create a new loan application",
            description = "Creates a new loan application in the system.",
            operationId = "createLoanApplication"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Loan application created successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(
                            name = "Success Response",
                            value = """
                                    {
                                      "success": true,
                                      "message": "Loan application created successfully"
                                    }"""
                    )
            )
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Validation errors in request body",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(
                            name = "Validation Error",
                            value = """
                                    {
                                      "success": false,
                                      "message": "Validation failed",
                                      "errors": [
                                        "Amount is required"
                                      ]
                                    }"""
                    )
            )
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(
                            name = "Server Error",
                            value = """
                                    {
                                      "success": false,
                                      "message": "Internal server error occurred"
                                    }"""
                    )
            )
    )
    @RequestBody(
            description = "Loan application information to create the loan application",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CreateLoanApplicationRequest.class),
                    examples = @ExampleObject(
                            name = "Valid Loan Application Request",
                            value = """
                                    {
                                      "amount": 1000000,
                                      "monthsTerm": 12,
                                      "email": "carlos.coral@example.com",
                                      "loanTypeId": "1234567890"
                                    }"""
                    )
            )
    )
    public Mono<ResponseEntity<ApiResponse<String>>> createLoanApplication(@org.springframework.web.bind.annotation.RequestBody CreateLoanApplicationRequest request) {
        return Mono.just(ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Hello World")));
    }
}
