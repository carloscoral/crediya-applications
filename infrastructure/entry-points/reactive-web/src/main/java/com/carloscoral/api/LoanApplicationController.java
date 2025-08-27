package com.carloscoral.api;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class LoanApplicationController {

    @PostMapping(path = "/loan-applications")
    public Mono<String> createLoanApplication() {
        return Mono.just("Hello World");
    }
}
