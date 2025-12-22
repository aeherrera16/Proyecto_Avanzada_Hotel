package edu.espe.springlab.exception.reactive;

public class ReactiveResourceNotFoundException extends RuntimeException {
    
    public ReactiveResourceNotFoundException(String message) {
        super(message);
    }
    
    public ReactiveResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
