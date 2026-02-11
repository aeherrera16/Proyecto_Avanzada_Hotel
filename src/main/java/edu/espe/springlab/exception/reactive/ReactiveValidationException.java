package edu.espe.springlab.exception.reactive;

// Excepción personalizada para errores de validación en operaciones reactivas
public class ReactiveValidationException extends RuntimeException {

    // Constructor que recibe un mensaje descriptivo del error de validación
    public ReactiveValidationException(String message) {
        super(message);
    }

    // Constructor que recibe un mensaje y la causa original (útil para encadenar excepciones)
    public ReactiveValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
