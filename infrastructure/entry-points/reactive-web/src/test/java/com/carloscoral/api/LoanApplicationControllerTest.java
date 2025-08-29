package com.carloscoral.api;

import com.carloscoral.api.dto.CreateLoanApplicationRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {LoanApplicationController.class})
@WebFluxTest
class LoanApplicationControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private LoanApplicationService loanApplicationService;

    @Test
    void testCreateLoanApplication() {
        CreateLoanApplicationRequest request = CreateLoanApplicationRequest.builder()
                .amount(new BigDecimal("10000.00"))
                .monthsTerm(12)
                .email("test@test.com")
                .loanTypeId("123e4567-e89b-12d3-a456-426614174000")
                .build();

        when(loanApplicationService.createLoanApplication(any(CreateLoanApplicationRequest.class)))
                .thenReturn(Mono.just("Loan application created successfully"));

        webTestClient.post()
                .uri("/api/v1/loan-applications")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo("Loan application created successfully");
    }

}
