package cl.ryodantech.amigodocente.models;

public class Evaluacion {
    private long id;
    private String nombre;          // Ej: "Prueba Unidad 1"
    private double puntajeMaximo;   // Ej: 60.0
    private String fecha;           // Guardaremos la fecha como String por ahora (YYYY-MM-DD)
    private long nivelAcademicoId;  // A qué nivel pertenece (obligatorio)
    private long cursoId;           // Opcional (0 si es para todo el nivel)
    private long configuracionId;   // Qué configuración UTP usa

    public Evaluacion() {}

    // Getters y Setters...
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public double getPuntajeMaximo() { return puntajeMaximo; }
    public void setPuntajeMaximo(double puntajeMaximo) { this.puntajeMaximo = puntajeMaximo; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public long getNivelAcademicoId() { return nivelAcademicoId; }
    public void setNivelAcademicoId(long nivelAcademicoId) { this.nivelAcademicoId = nivelAcademicoId; }
    public long getCursoId() { return cursoId; }
    public void setCursoId(long cursoId) { this.cursoId = cursoId; }
    public long getConfiguracionId() { return configuracionId; }
    public void setConfiguracionId(long configuracionId) { this.configuracionId = configuracionId; }
}