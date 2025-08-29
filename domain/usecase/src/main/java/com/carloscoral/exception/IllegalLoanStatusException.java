package com.carloscoral.exception;

public class IllegalLoanStatusException extends RuntimeException {
    public IllegalLoanStatusException(String message) {
        super(message);
    }
}
