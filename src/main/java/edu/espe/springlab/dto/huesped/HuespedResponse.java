package edu.espe.springlab.dto.huesped;

public class HuespedResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String cedula; // CAMBIO: de 'identificacion' a 'cedula'
    private String email;
    private String telefono;
    private String nacionalidad;

    // Constructor por defecto
    public HuespedResponse() {
    }

    // Constructor con todos los campos
    public HuespedResponse(Long id, String nombre, String apellido, String cedula, String email, String telefono, String nacionalidad) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedula = cedula;
        this.email = email;
        this.telefono = telefono;
        this.nacionalidad = nacionalidad;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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