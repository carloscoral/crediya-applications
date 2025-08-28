package com.carloscoral.r2dbc;

import com.carloscoral.model.loanstatus.LoanStatus;
import com.carloscoral.r2dbc.entity.LoanStatusEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoanStatusReactiveRepositoryAdapter Tests")
class LoanStatusReactiveRepositoryAdapterTest {

    @Mock
    private LoanStatusReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    private LoanStatusReactiveRepositoryAdapter adapter;
    private UUID testId;
    private LoanStatus testLoanStatus;
    private LoanStatusEntity testLoanStatusEntity;

    @BeforeEach
    void setUp() {
        adapter = new LoanStatusReactiveRepositoryAdapter(repository, mapper);
        testId = UUID.randomUUID();
        
        testLoanStatus = LoanStatus.builder()
                .id(testId)
                .name("Pendiente de revisión")
                .description("Solicitud pendiente de revisión por el equipo de crédito")
                .build();

        testLoanStatusEntity = LoanStatusEntity.builder()
                .id(testId)
                .name("Pendiente de revisión")
                .description("Solicitud pendiente de revisión por el equipo de crédito")
                .build();
    }

    @Test
    @DisplayName("Should find loan status by name successfully")
    void shouldFindLoanStatusByNameSuccessfully() {
        String statusName = "Pendiente de revisión";
        when(repository.findByName(statusName)).thenReturn(Mono.just(testLoanStatusEntity));
        when(mapper.mapBuilder(eq(testLoanStatusEntity), eq(LoanStatus.LoanStatusBuilder.class)))
                .thenReturn(testLoanStatus.toBuilder());

        StepVerifier.create(adapter.findByName(statusName))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(testLoanStatus.getId(), result.getId());
                    assertEquals(testLoanStatus.getName(), result.getName());
                    assertEquals(testLoanStatus.getDescription(), result.getDescription());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty when loan status not found by name")
    void shouldReturnEmptyWhenLoanStatusNotFoundByName() {
        String statusName = "Non-existent Status";
        when(repository.findByName(statusName)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findByName(statusName))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find approved status by name")
    void shouldFindApprovedStatusByName() {
        String statusName = "Aprobado";
        LoanStatus approvedStatus = LoanStatus.builder()
                .id(UUID.randomUUID())
                .name("Aprobado")
                .description("Solicitud aprobada y lista para desembolso")
                .build();
        
        LoanStatusEntity approvedEntity = LoanStatusEntity.builder()
                .id(approvedStatus.getId())
                .name("Aprobado")
                .description("Solicitud aprobada y lista para desembolso")
                .build();

        when(repository.findByName(statusName)).thenReturn(Mono.just(approvedEntity));
        when(mapper.mapBuilder(eq(approvedEntity), eq(LoanStatus.LoanStatusBuilder.class)))
                .thenReturn(approvedStatus.toBuilder());

        StepVerifier.create(adapter.findByName(statusName))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(approvedStatus.getId(), result.getId());
                    assertEquals("Aprobado", result.getName());
                    assertEquals("Solicitud aprobada y lista para desembolso", result.getDescription());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find rejected status by name")
    void shouldFindRejectedStatusByName() {
        String statusName = "Rechazado";
        LoanStatus rejectedStatus = LoanStatus.builder()
                .id(UUID.randomUUID())
                .name("Rechazado")
                .description("Solicitud rechazada por no cumplir criterios")
                .build();
        
        LoanStatusEntity rejectedEntity = LoanStatusEntity.builder()
                .id(rejectedStatus.getId())
                .name("Rechazado")
                .description("Solicitud rechazada por no cumplir criterios")
                .build();

        when(repository.findByName(statusName)).thenReturn(Mono.just(rejectedEntity));
        when(mapper.mapBuilder(eq(rejectedEntity), eq(LoanStatus.LoanStatusBuilder.class)))
                .thenReturn(rejectedStatus.toBuilder());

        StepVerifier.create(adapter.findByName(statusName))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(rejectedStatus.getId(), result.getId());
                    assertEquals("Rechazado", result.getName());
                    assertEquals("Solicitud rechazada por no cumplir criterios", result.getDescription());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle null name gracefully")
    void shouldHandleNullNameGracefully() {
        String nullName = null;
        when(repository.findByName(nullName)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findByName(nullName))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle empty name gracefully")
    void shouldHandleEmptyNameGracefully() {
        String emptyName = "";
        when(repository.findByName(emptyName)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findByName(emptyName))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle repository error gracefully")
    void shouldHandleRepositoryErrorGracefully() {
        String statusName = "Pendiente de revisión";
        when(repository.findByName(statusName)).thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(adapter.findByName(statusName))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle mapping error gracefully")
    void shouldHandleMappingErrorGracefully() {
        String statusName = "Pendiente de revisión";
        when(repository.findByName(statusName)).thenReturn(Mono.just(testLoanStatusEntity));
        when(mapper.mapBuilder(eq(testLoanStatusEntity), eq(LoanStatus.LoanStatusBuilder.class)))
                .thenThrow(new RuntimeException("Mapping error"));

        StepVerifier.create(adapter.findByName(statusName))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Should inherit ReactiveAdapterOperations methods")
    void shouldInheritReactiveAdapterOperationsMethods() {
        when(repository.findById(testId)).thenReturn(Mono.just(testLoanStatusEntity));
        when(mapper.mapBuilder(eq(testLoanStatusEntity), eq(LoanStatus.LoanStatusBuilder.class)))
                .thenReturn(testLoanStatus.toBuilder());

        StepVerifier.create(adapter.findById(testId))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(testLoanStatus.getId(), result.getId());
                    assertEquals(testLoanStatus.getName(), result.getName());
                    assertEquals(testLoanStatus.getDescription(), result.getDescription());
                })
                .verifyComplete();
    }
}
