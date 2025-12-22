package edu.espe.springlab.exception.reactive;

public class ReactiveValidationException extends RuntimeException {
    
    public ReactiveValidationException(String message) {
        super(message);
    }
    
    public ReactiveValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
