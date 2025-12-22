package edu.espe.springlab.exception.reactive;

// Excepción personalizada para manejar recursos no encontrados en un contexto reactivo
public class ReactiveResourceNotFoundException extends RuntimeException {

    // Constructor que recibe un mensaje de error
    public ReactiveResourceNotFoundException(String message) {
        super(message);
    }

    // Constructor que recibe un mensaje de error y la causa original (excepción anidada)
    public ReactiveResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
