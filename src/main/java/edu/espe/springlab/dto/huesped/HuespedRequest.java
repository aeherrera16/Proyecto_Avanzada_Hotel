package edu.espe.springlab.dto.huesped;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class HuespedRequest {
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(max = 100, message = "El apellido no puede exceder los 100 caracteres")
    private String apellido;

    @NotBlank(message = "El número de identificación no puede estar vacío")
    @Size(min = 10, max = 13, message = "El número de identificación debe tener entre 10 y 13 caracteres")
    private String cedula; // CAMBIO: de 'identificacion' a 'cedula'

    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Email(message = "El correo electrónico debe ser válido")
    @Size(max = 100, message = "El correo electrónico no puede exceder los 100 caracteres")
    private String email;

    @NotBlank(message = "El número de teléfono no puede estar vacío")
    @Pattern(regexp = "^[0-9]{10}$", message = "El número de teléfono debe tener 10 dígitos numéricos")
    private String telefono;

    @NotBlank(message = "La nacionalidad no puede estar vacía")
    @Size(max = 100, message = "La nacionalidad no puede exceder los 100 caracteres")
    private String nacionalidad;

    // Constructor por defecto
    public HuespedRequest() {
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCedula() { // CAMBIO: de getIdentificacion() a getCedula()
        return cedula;
    }

    public void setCedula(String cedula) { // CAMBIO: de setIdentificacion() a setCedula()
        this.cedula = cedula;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }
}