package com.carloscoral.api.validation;

import com.carloscoral.api.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GenericValidator Tests")
class GenericValidatorTest {

    @Mock
    private Validator validator;

    @Mock
    private ConstraintViolation<TestObject> violation1;

    @Mock
    private ConstraintViolation<TestObject> violation2;

    private GenericValidator genericValidator;

    @BeforeEach
    void setUp() {
        genericValidator = new GenericValidator(validator);
    }

    @Test
    @DisplayName("Should successfully validate valid object")
    void shouldSuccessfullyValidateValidObject() {
        TestObject validObject = new TestObject("Valid Name", "valid@email.com");
        when(validator.validate(validObject)).thenReturn(Set.of());

        StepVerifier.create(genericValidator.validate(validObject))
                .expectNext(validObject)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw ValidationException for invalid object with single violation")
    void shouldThrowValidationExceptionForInvalidObjectWithSingleViolation() {
        TestObject invalidObject = new TestObject("", "invalid-email");
        
        when(violation1.getPropertyPath()).thenReturn(mockPropertyPath("name"));
        when(violation1.getMessage()).thenReturn("must not be blank");
        when(validator.validate(invalidObject)).thenReturn(Set.of(violation1));

        StepVerifier.create(genericValidator.validate(invalidObject))
                .expectErrorMatches(error -> {
                    if (!(error instanceof ValidationException)) return false;
                    
                    ValidationException validationException = (ValidationException) error;
                    List<String> errors = validationException.getValidationErrors();
                    
                    return errors.size() == 1 && 
                           errors.contains("name: must not be blank");
                })
                .verify();
    }

    @Test
    @DisplayName("Should throw ValidationException for invalid object with multiple violations")
    void shouldThrowValidationExceptionForInvalidObjectWithMultipleViolations() {
        TestObject invalidObject = new TestObject("", "");
        
        when(violation1.getPropertyPath()).thenReturn(mockPropertyPath("name"));
        when(violation1.getMessage()).thenReturn("must not be blank");
        when(violation2.getPropertyPath()).thenReturn(mockPropertyPath("email"));
        when(violation2.getMessage()).thenReturn("must not be blank");
        when(validator.validate(invalidObject)).thenReturn(Set.of(violation1, violation2));

        StepVerifier.create(genericValidator.validate(invalidObject))
                .expectErrorMatches(error -> {
                    if (!(error instanceof ValidationException)) return false;
                    
                    ValidationException validationException = (ValidationException) error;
                    List<String> errors = validationException.getValidationErrors();
                    
                    return errors.size() == 2 && 
                           errors.contains("name: must not be blank") &&
                           errors.contains("email: must not be blank");
                })
                .verify();
    }

    @Test
    @DisplayName("Should handle runtime exception from validator")
    void shouldHandleRuntimeExceptionFromValidator() {
        TestObject testObject = new TestObject("test", "test@email.com");
        when(validator.validate(testObject)).thenThrow(new RuntimeException("Validator exception"));

        StepVerifier.create(genericValidator.validate(testObject))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle validation with complex constraint violations")
    void shouldHandleValidationWithComplexConstraintViolations() {
        TestObject invalidObject = new TestObject("x", "invalid-email");
        
        when(violation1.getPropertyPath()).thenReturn(mockPropertyPath("name"));
        when(violation1.getMessage()).thenReturn("size must be between 2 and 50");
        when(violation2.getPropertyPath()).thenReturn(mockPropertyPath("email"));
        when(violation2.getMessage()).thenReturn("must be a well-formed email address");
        when(validator.validate(invalidObject)).thenReturn(Set.of(violation1, violation2));

        StepVerifier.create(genericValidator.validate(invalidObject))
                .expectErrorMatches(error -> {
                    if (!(error instanceof ValidationException)) return false;
                    
                    ValidationException validationException = (ValidationException) error;
                    List<String> errors = validationException.getValidationErrors();
                    
                    return errors.size() == 2 && 
                           errors.contains("name: size must be between 2 and 50") &&
                           errors.contains("email: must be a well-formed email address");
                })
                .verify();
    }

    @Test
    @DisplayName("Should validate different object types")
    void shouldValidateDifferentObjectTypes() {
        String stringObject = "test string";
        when(validator.validate(stringObject)).thenReturn(Set.of());

        StepVerifier.create(genericValidator.validate(stringObject))
                .expectNext(stringObject)
                .verifyComplete();
    }



    @Test
    @DisplayName("Should preserve object reference after successful validation")
    void shouldPreserveObjectReferenceAfterSuccessfulValidation() {
        TestObject originalObject = new TestObject("Valid Name", "valid@email.com");
        when(validator.validate(originalObject)).thenReturn(Set.of());

        StepVerifier.create(genericValidator.validate(originalObject))
                .assertNext(validatedObject -> {
                    assert validatedObject == originalObject;
                    assert validatedObject.getName().equals("Valid Name");
                    assert validatedObject.getEmail().equals("valid@email.com");
                })
                .verifyComplete();
    }

    private jakarta.validation.Path mockPropertyPath(String propertyName) {
        return new jakarta.validation.Path() {
            @Override
            public Iterator<Node> iterator() {
                return null;
            }

            @Override
            public String toString() {
                return propertyName;
            }
        };
    }

    public static class TestObject {
        @NotBlank(message = "must not be blank")
        @Size(min = 2, max = 50, message = "size must be between 2 and 50")
        private String name;

        @NotBlank(message = "must not be blank")
        private String email;

        public TestObject(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }
}
