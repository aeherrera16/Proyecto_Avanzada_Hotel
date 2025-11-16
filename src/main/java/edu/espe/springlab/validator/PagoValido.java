package edu.espe.springlab.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PagoValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PagoValido {
    String message() default "La fecha de pago debe estar dentro del rango de fechas de la reserva";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}