package com.carloscoral.model.loanstatus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoanStatusEnum Tests")
class LoanStatusEnumTest {

    @Test
    @DisplayName("Should return correct display name for each enum value")
    void shouldReturnCorrectDisplayName() {
        assertEquals("Pendiente de revisión", LoanStatusEnum.PENDING_REVIEW.getDisplayName());
        assertEquals("Aprobado", LoanStatusEnum.APPROVED.getDisplayName());
        assertEquals("Rechazado", LoanStatusEnum.REJECTED.getDisplayName());
        assertEquals("Desembolsado", LoanStatusEnum.DISBURSED.getDisplayName());
        assertEquals("Cerrado", LoanStatusEnum.CLOSED.getDisplayName());
        assertEquals("Cancelado", LoanStatusEnum.CANCELLED.getDisplayName());
    }

    @ParameterizedTest
    @EnumSource(LoanStatusEnum.class)
    @DisplayName("Should find enum by display name for all values")
    void shouldFindEnumByDisplayName(LoanStatusEnum expectedEnum) {
        String displayName = expectedEnum.getDisplayName();

        LoanStatusEnum actualEnum = LoanStatusEnum.fromDisplayName(displayName);

        assertEquals(expectedEnum, actualEnum);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when display name not found")
    void shouldThrowExceptionWhenDisplayNameNotFound() {
        String nonExistentDisplayName = "Non-existent Status";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> LoanStatusEnum.fromDisplayName(nonExistentDisplayName)
        );

        assertEquals("No enum constant with display name: " + nonExistentDisplayName, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when display name is null")
    void shouldThrowExceptionWhenDisplayNameIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> LoanStatusEnum.fromDisplayName(null)
        );

        assertEquals("No enum constant with display name: null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when display name is empty")
    void shouldThrowExceptionWhenDisplayNameIsEmpty() {
        String emptyDisplayName = "";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> LoanStatusEnum.fromDisplayName(emptyDisplayName)
        );

        assertEquals("No enum constant with display name: ", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "pendiente de revisión",  // lowercase
            "APROBADO",              // uppercase
            " Rechazado ",           // with spaces
            "Desembolsado "          // trailing space
    })
    @DisplayName("Should be case and space sensitive")
    void shouldBeCaseAndSpaceSensitive(String displayName) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> LoanStatusEnum.fromDisplayName(displayName)
        );

        assertEquals("No enum constant with display name: " + displayName, exception.getMessage());
    }

    @Test
    @DisplayName("Should return all display names")
    void shouldReturnAllDisplayNames() {
        String[] displayNames = LoanStatusEnum.getAllDisplayNames();

        assertNotNull(displayNames);
        assertEquals(6, displayNames.length);
        
        assertTrue(java.util.Arrays.asList(displayNames).contains("Pendiente de revisión"));
        assertTrue(java.util.Arrays.asList(displayNames).contains("Aprobado"));
        assertTrue(java.util.Arrays.asList(displayNames).contains("Rechazado"));
        assertTrue(java.util.Arrays.asList(displayNames).contains("Desembolsado"));
        assertTrue(java.util.Arrays.asList(displayNames).contains("Cerrado"));
        assertTrue(java.util.Arrays.asList(displayNames).contains("Cancelado"));
    }

    @Test
    @DisplayName("Should return all display names in correct order")
    void shouldReturnDisplayNamesInCorrectOrder() {
        String[] displayNames = LoanStatusEnum.getAllDisplayNames();

        assertArrayEquals(
                new String[]{
                        "Pendiente de revisión",
                        "Aprobado", 
                        "Rechazado",
                        "Desembolsado",
                        "Cerrado",
                        "Cancelado"
                },
                displayNames
        );
    }

    @ParameterizedTest
    @EnumSource(LoanStatusEnum.class)
    @DisplayName("Should return display name when toString is called")
    void shouldReturnDisplayNameWhenToStringIsCalled(LoanStatusEnum status) {
        String stringRepresentation = status.toString();

        assertEquals(status.getDisplayName(), stringRepresentation);
    }

    @Test
    @DisplayName("Should have exactly 6 enum values")
    void shouldHaveExactlySixEnumValues() {
        LoanStatusEnum[] values = LoanStatusEnum.values();

        assertEquals(6, values.length);
    }

    @Test
    @DisplayName("Should verify enum constants exist")
    void shouldVerifyEnumConstantsExist() {
        assertNotNull(LoanStatusEnum.PENDING_REVIEW);
        assertNotNull(LoanStatusEnum.APPROVED);
        assertNotNull(LoanStatusEnum.REJECTED);
        assertNotNull(LoanStatusEnum.DISBURSED);
        assertNotNull(LoanStatusEnum.CLOSED);
        assertNotNull(LoanStatusEnum.CANCELLED);
    }

    @Test
    @DisplayName("Should maintain consistent behavior between getDisplayName and toString")
    void shouldMaintainConsistentBehaviorBetweenGetDisplayNameAndToString() {
        for (LoanStatusEnum status : LoanStatusEnum.values()) {
            assertEquals(status.getDisplayName(), status.toString(),
                    "toString() should return the same value as getDisplayName() for " + status.name());
        }
    }

    @Test
    @DisplayName("Should verify specific enum name mappings")
    void shouldVerifySpecificEnumNameMappings() {
        assertEquals("PENDING_REVIEW", LoanStatusEnum.PENDING_REVIEW.name());
        assertEquals("APPROVED", LoanStatusEnum.APPROVED.name());
        assertEquals("REJECTED", LoanStatusEnum.REJECTED.name());
        assertEquals("DISBURSED", LoanStatusEnum.DISBURSED.name());
        assertEquals("CLOSED", LoanStatusEnum.CLOSED.name());
        assertEquals("CANCELLED", LoanStatusEnum.CANCELLED.name());
    }
}
