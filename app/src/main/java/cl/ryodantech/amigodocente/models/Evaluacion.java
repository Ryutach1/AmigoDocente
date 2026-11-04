package cl.ryodantech.amigodocente.models;

public class Evaluacion {
    private long id;
    private String nombre;          // Ej: "Prueba Unidad 1"
    private int puntajeMaximo;   // Ej: 60.0
    private String fecha;           // Guardaremos la fecha como String por ahora (YYYY-MM-DD)
    private long nivelAcademicoId;  // A qué nivel pertenece (obligatorio)

    private long configuracionId;   // Qué configuración UTP usa

    public Evaluacion() {}

    // Getters y Setters...
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getPuntajeMaximo() { return puntajeMaximo; }
    public void setPuntajeMaximo(int puntajeMaximo) { this.puntajeMaximo = puntajeMaximo; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public long getNivelAcademicoId() { return nivelAcademicoId; }
    public void setNivelAcademicoId(long nivelAcademicoId) { this.nivelAcademicoId = nivelAcademicoId; }


    public long getConfiguracionId() { return configuracionId; }
    public void setConfiguracionId(long configuracionId) { this.configuracionId = configuracionId; }
}