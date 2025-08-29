package com.carloscoral.consumer;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;
import reactor.core.publisher.Mono;
import com.carloscoral.model.user.gateways.ValidationUserGateway;

@Service
@RequiredArgsConstructor
public class UserRestConsumer implements ValidationUserGateway {
    private final WebClient client;

    @CircuitBreaker(name = "validateUser")
    @Override
    public Mono<Boolean> validateByEmail(String email) {
        return client
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/users/validate")
                        .queryParam("email", email)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ObjectResponse<Boolean>>() {})
                .map(ObjectResponse::getData);
    }
}
