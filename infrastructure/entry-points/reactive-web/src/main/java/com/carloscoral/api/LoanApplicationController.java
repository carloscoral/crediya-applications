package com.carloscoral.api;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class LoanApplicationController {
//    private final MyUseCase useCase;


    @GetMapping(path = "/usecase/path")
    public Mono<String> commandName() {
//      return useCase.doAction();
        return Mono.just("");
    }
}
