package cl.ryodantech.amigodocente.models;

public class Curso {
    private long id;
    private String nombre; // Ej: "A", "B"
    private int año;       // Ej: 2026
    private long nivelAcademicoId; // Relación al nivel

    public Curso() {}

    public Curso(String nombre, int año, long nivelAcademicoId) {
        this.nombre = nombre;
        this.año = año;
        this.nivelAcademicoId = nivelAcademicoId;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getAño() { return año; }
    public void setAño(int año) { this.año = año; }
    public long getNivelAcademicoId() { return nivelAcademicoId; }
    public void setNivelAcademicoId(long nivelAcademicoId) { this.nivelAcademicoId = nivelAcademicoId; }
}