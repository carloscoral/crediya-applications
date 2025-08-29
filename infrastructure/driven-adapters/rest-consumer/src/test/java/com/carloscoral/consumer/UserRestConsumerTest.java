package com.carloscoral.consumer;


import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;
import java.io.IOException;


class UserRestConsumerTest {

    private static UserRestConsumer userRestConsumer;

    private static MockWebServer mockBackEnd;


    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        var webClient = WebClient.builder().baseUrl(mockBackEnd.url("/").toString()).build();
        userRestConsumer = new UserRestConsumer(webClient);
    }

    @AfterAll
    static void tearDown() throws IOException {

        mockBackEnd.shutdown();
    }

    @Test
    @DisplayName("Validate user exists - should return true")
    void validateUserExists() {
        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody("{\"success\": true, \"message\": \"User found\", \"data\": true, \"errors\": null}"));
        
        var response = userRestConsumer.validateByEmail("test@test.com");

        StepVerifier.create(response)
                .expectNextMatches(result -> result.equals(true))
                .verifyComplete();
    }

    @Test
    @DisplayName("Validate user does not exist - should return false")
    void validateUserDoesNotExist() {
        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody("{\"success\": true, \"message\": \"User not found\", \"data\": false, \"errors\": null}"));
        
        var response = userRestConsumer.validateByEmail("nonexistent@test.com");

        StepVerifier.create(response)
                .expectNextMatches(result -> result.equals(false))
                .verifyComplete();
    }
}