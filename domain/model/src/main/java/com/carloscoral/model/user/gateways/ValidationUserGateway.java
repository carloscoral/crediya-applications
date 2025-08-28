package com.carloscoral.model.user.gateways;

import reactor.core.publisher.Mono;

public interface ValidationUserGateway {
    Mono<Boolean> validateByEmail(String email);
}
