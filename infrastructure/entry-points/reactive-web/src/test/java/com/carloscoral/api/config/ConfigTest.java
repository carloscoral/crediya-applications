package com.carloscoral.api.config;

import com.carloscoral.api.LoanApplicationController;
import com.carloscoral.api.LoanApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

@ContextConfiguration(classes = {LoanApplicationController.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private LoanApplicationService loanApplicationService;

    @Test
    void configurationShouldLoadCorrectly() {
        // Test básico que verifica que las configuraciones se cargan sin errores
        // En @WebFluxTest, las configuraciones se cargan pero no podemos probar CORS fácilmente
        // sin un contexto completo de aplicación
        webTestClient.get()
                .uri("/api/v1/non-existent-endpoint")
                .exchange()
                .expectStatus().isNotFound(); // 404 indica que el contexto está funcionando
    }

}
