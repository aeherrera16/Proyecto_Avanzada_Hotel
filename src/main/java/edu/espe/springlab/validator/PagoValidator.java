package edu.espe.springlab.validator;

import edu.espe.springlab.dto.pago.PagoRequest;
import edu.espe.springlab.repository.ReservaRepository;
import edu.espe.springlab.web.advice.NotFoundException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PagoValidator implements ConstraintValidator<PagoValido, PagoRequest> {

    private final ReservaRepository reservaRepository;

    public PagoValidator(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    @Override
    public boolean isValid(PagoRequest request, ConstraintValidatorContext context) {
        boolean isValid = true;

        // Si los campos básicos son nulos, dejamos que @NotNull se encargue
        if (request.getReservaId() == null || request.getFechaPago() == null || request.getMonto() == null) {
            return true;
        }

        try {
            var reserva = reservaRepository.findById(request.getReservaId())
                    .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));

            // 🔍 VALIDACIÓN 1: Fecha de pago dentro del rango de reserva
            var fechaPago = request.getFechaPago().toLocalDate();
            var fechaEntradaReserva = reserva.getFechaEntrada();
            var fechaSalidaReserva = reserva.getFechaSalida();

            if (fechaPago.isBefore(fechaEntradaReserva) || fechaPago.isAfter(fechaSalidaReserva)) {
                isValid = false;
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "La fecha de pago (" + fechaPago + ") debe estar entre " +
                                fechaEntradaReserva + " y " + fechaSalidaReserva + " de la reserva"
                ).addPropertyNode("fechaPago").addConstraintViolation();
            }

            //  VALIDACIÓN 2: Monto debe ser EXACTAMENTE igual al precioTotal de la reserva
            var montoPago = request.getMonto();
            var precioTotalReserva = reserva.getPrecioTotal();

            if (!montoPago.equals(precioTotalReserva)) {
                isValid = false;
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "El monto del pago (" + montoPago + ") debe ser exactamente igual al precio total de la reserva: " + precioTotalReserva
                ).addPropertyNode("monto").addConstraintViolation();
            }

            // 🔍 VALIDACIÓN 3: Fecha de pago no puede ser en el futuro lejano (máximo 1 año)
            var fechaPagoCompleta = request.getFechaPago();
            var maxFechaPermitida = LocalDateTime.now().plusYears(1);

            if (fechaPagoCompleta.isAfter(maxFechaPermitida)) {
                isValid = false;
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "La fecha de pago no puede ser más de 1 año en el futuro"
                ).addPropertyNode("fechaPago").addConstraintViolation();
            }

            return isValid;

        } catch (NotFoundException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("No se encontró la reserva con ID: " + request.getReservaId())
                    .addPropertyNode("reservaId").addConstraintViolation();
            return false;
        } catch (Exception e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Error al validar el pago: " + e.getMessage())
                    .addConstraintViolation();
            return false;
        }
    }
}