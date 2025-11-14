package entities;

import java.time.LocalDate;

public class Paciente {

    private long idPaciente;
    private String nombre;
    private String apellido;
    private int dni;
    private LocalDate fechaNacimiento;
    private HistoriaClinica historiaClinica;

    public Paciente(long idPaciente, String nombre, String apellido,
                    int dni, LocalDate fechaNacimiento, HistoriaClinica historiaClinica) {

        this.idPaciente = idPaciente;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
        this.historiaClinica = historiaClinica;
    }

    // ===========================
    // GETTERS Y SETTERS CORRECTOS
    // ===========================

    public long getId() {
        return idPaciente;
    }

    public void setId(long idPaciente) {
        this.idPaciente = idPaciente;
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

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public HistoriaClinica getHistoriaClinica() {
        return historiaClinica;
    }

    public void setHistoriaClinica(HistoriaClinica historiaClinica) {
        this.historiaClinica = historiaClinica;
    }

    @Override
    public String toString() {
        return "Paciente{" +
                "idPaciente=" + idPaciente +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", dni=" + dni +
                ", fechaNacimiento=" + fechaNacimiento +
                ", historiaClinica=" + historiaClinica +
                '}';
    }
}
