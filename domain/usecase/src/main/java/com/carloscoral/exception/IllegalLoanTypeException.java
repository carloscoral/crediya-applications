package com.carloscoral.exception;

public class IllegalLoanTypeException extends RuntimeException {
    public IllegalLoanTypeException(String message) {
        super(message);
    }
}
