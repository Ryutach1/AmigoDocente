package cl.ryodantech.amigodocente.models;

public class NivelAcademico {
    private long id;
    private String nombre; // Ej: "8° Básico", "1° Medio"

    public NivelAcademico() {}

    public NivelAcademico(String nombre) {
        this.nombre = nombre;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
