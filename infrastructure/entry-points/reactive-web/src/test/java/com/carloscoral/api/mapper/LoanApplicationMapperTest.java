package com.carloscoral.api.mapper;

import com.carloscoral.api.dto.CreateLoanApplicationRequest;
import com.carloscoral.model.loanapplication.LoanApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoanApplicationMapper Tests")
class LoanApplicationMapperTest {

    private LoanApplicationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(LoanApplicationMapper.class);
    }

    @Test
    @DisplayName("Should map CreateLoanApplicationRequest to LoanApplication successfully")
    void shouldMapCreateLoanApplicationRequestToLoanApplicationSuccessfully() {
        String loanTypeIdString = "123e4567-e89b-12d3-a456-426614174000";
        CreateLoanApplicationRequest request = CreateLoanApplicationRequest.builder()
                .amount(new BigDecimal("15000.50"))
                .monthsTerm(24)
                .email("test.user@example.com")
                .loanTypeId(loanTypeIdString)
                .build();

        LoanApplication result = mapper.toLoanApplication(request);

        assertNotNull(result, "Mapped LoanApplication should not be null");
        assertEquals(request.getAmount(), result.getAmount(), "Amount should be mapped correctly");
        assertEquals(request.getMonthsTerm(), result.getMonthsTerm(), "MonthsTerm should be mapped correctly");
        assertEquals(request.getEmail(), result.getEmail(), "Email should be mapped correctly");
        assertEquals(UUID.fromString(loanTypeIdString), result.getLoanTypeId(), "LoanTypeId should be converted from String to UUID");
        assertNull(result.getLoanStatusId(), "LoanStatusId should be null as it's ignored in mapping");
    }

    @Test
    @DisplayName("Should handle null request gracefully")
    void shouldHandleNullRequestGracefully() {
        CreateLoanApplicationRequest nullRequest = null;

        LoanApplication result = mapper.toLoanApplication(nullRequest);

        assertNull(result, "Result should be null when input is null");
    }

    @Test
    @DisplayName("Should convert string UUID to UUID object")
    void shouldConvertStringUuidToUuidObject() {
        String validUuidString = "a1b2c3d4-e5f6-7890-abcd-ef1234567890";
        CreateLoanApplicationRequest request = CreateLoanApplicationRequest.builder()
                .amount(new BigDecimal("10000.00"))
                .monthsTerm(12)
                .email("uuid.test@example.com")
                .loanTypeId(validUuidString)
                .build();

        LoanApplication result = mapper.toLoanApplication(request);

        assertNotNull(result, "Result should not be null");
        assertNotNull(result.getLoanTypeId(), "LoanTypeId should not be null");
        assertEquals(UUID.fromString(validUuidString), result.getLoanTypeId(), "UUID conversion should be correct");
        assertEquals(validUuidString, result.getLoanTypeId().toString(), "UUID toString should match original string");
    }

    @Test
    @DisplayName("Should handle minimum values correctly")
    void shouldHandleMinimumValuesCorrectly() {
        CreateLoanApplicationRequest request = CreateLoanApplicationRequest.builder()
                .amount(new BigDecimal("0.01"))
                .monthsTerm(1)
                .email("min@test.com")
                .loanTypeId("00000000-0000-0000-0000-000000000001")
                .build();

        LoanApplication result = mapper.toLoanApplication(request);

        assertNotNull(result, "Result should not be null");
        assertEquals(new BigDecimal("0.01"), result.getAmount(), "Minimum amount should be mapped correctly");
        assertEquals(1, result.getMonthsTerm(), "Minimum months term should be mapped correctly");
        assertEquals("min@test.com", result.getEmail(), "Email should be mapped correctly");
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), result.getLoanTypeId(), "Minimum UUID should be converted correctly");
    }

    @Test
    @DisplayName("Should handle maximum values correctly")
    void shouldHandleMaximumValuesCorrectly() {
        CreateLoanApplicationRequest request = CreateLoanApplicationRequest.builder()
                .amount(new BigDecimal("9999999999999.99"))
                .monthsTerm(360)
                .email("very.long.email.address.for.testing.maximum.length.validation@verylongdomainname.example.com")
                .loanTypeId("ffffffff-ffff-ffff-ffff-ffffffffffff")
                .build();

        LoanApplication result = mapper.toLoanApplication(request);

        assertNotNull(result, "Result should not be null");
        assertEquals(new BigDecimal("9999999999999.99"), result.getAmount(), "Maximum amount should be mapped correctly");
        assertEquals(360, result.getMonthsTerm(), "Maximum months term should be mapped correctly");
        assertEquals("very.long.email.address.for.testing.maximum.length.validation@verylongdomainname.example.com", 
                    result.getEmail(), "Long email should be mapped correctly");
        assertEquals(UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff"), result.getLoanTypeId(), "Maximum UUID should be converted correctly");
    }

    @Test
    @DisplayName("Should handle various UUID formats correctly")
    void shouldHandleVariousUuidFormatsCorrectly() {
        String[] uuidFormats = {
            "123e4567-e89b-12d3-a456-426614174000", // Standard format
            "12345678-1234-5678-9abc-123456789abc", // Hex characters
            "00000000-0000-0000-0000-000000000000", // All zeros
            "FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF"  // All F's (uppercase)
        };

        for (String uuidString : uuidFormats) {
            CreateLoanApplicationRequest request = CreateLoanApplicationRequest.builder()
                    .amount(new BigDecimal("5000.00"))
                    .monthsTerm(6)
                    .email("uuid.format.test@example.com")
                    .loanTypeId(uuidString)
                    .build();

            LoanApplication result = mapper.toLoanApplication(request);

            assertNotNull(result, "Result should not be null for UUID: " + uuidString);
            assertEquals(UUID.fromString(uuidString), result.getLoanTypeId(), 
                        "UUID should be converted correctly for format: " + uuidString);
        }
    }

    @Test
    @DisplayName("Should throw exception for invalid UUID format")
    void shouldThrowExceptionForInvalidUuidFormat() {
        CreateLoanApplicationRequest request = CreateLoanApplicationRequest.builder()
                .amount(new BigDecimal("1000.00"))
                .monthsTerm(12)
                .email("invalid.uuid@test.com")
                .loanTypeId("invalid-uuid-format")
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            mapper.toLoanApplication(request);
        }, "Should throw IllegalArgumentException for invalid UUID format");
    }

    @Test
    @DisplayName("Should preserve decimal precision in amount mapping")
    void shouldPreserveDecimalPrecisionInAmountMapping() {
        CreateLoanApplicationRequest request = CreateLoanApplicationRequest.builder()
                .amount(new BigDecimal("12345.67"))
                .monthsTerm(18)
                .email("precision.test@example.com")
                .loanTypeId("123e4567-e89b-12d3-a456-426614174000")
                .build();

        LoanApplication result = mapper.toLoanApplication(request);

        assertNotNull(result, "Result should not be null");
        assertEquals(new BigDecimal("12345.67"), result.getAmount(), "Decimal precision should be preserved");
        assertEquals(0, new BigDecimal("12345.67").compareTo(result.getAmount()), "BigDecimal values should be exactly equal");
    }

    @Test
    @DisplayName("Should ensure loanStatusId is always null after mapping")
    void shouldEnsureLoanStatusIdIsAlwaysNullAfterMapping() {
        CreateLoanApplicationRequest request = CreateLoanApplicationRequest.builder()
                .amount(new BigDecimal("8000.00"))
                .monthsTerm(30)
                .email("status.test@example.com")
                .loanTypeId("987fcdeb-51a2-43d1-b456-426614174000")
                .build();

        LoanApplication result = mapper.toLoanApplication(request);

        assertNotNull(result, "Result should not be null");
        assertNull(result.getLoanStatusId(), "LoanStatusId should always be null as it's ignored in mapping");
    }

    @Test
    @DisplayName("Should handle special characters in email correctly")
    void shouldHandleSpecialCharactersInEmailCorrectly() {
        String emailWithSpecialChars = "user+test.123@sub-domain.example-site.co.uk";
        CreateLoanApplicationRequest request = CreateLoanApplicationRequest.builder()
                .amount(new BigDecimal("2500.75"))
                .monthsTerm(9)
                .email(emailWithSpecialChars)
                .loanTypeId("456e7890-a1b2-34c5-d678-901234567890")
                .build();

        LoanApplication result = mapper.toLoanApplication(request);

        assertNotNull(result, "Result should not be null");
        assertEquals(emailWithSpecialChars, result.getEmail(), "Email with special characters should be preserved");
    }
}
