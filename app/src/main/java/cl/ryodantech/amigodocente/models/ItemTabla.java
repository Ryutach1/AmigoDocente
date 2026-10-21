package cl.ryodantech.amigodocente.models;

public class ItemTabla {
    private long id;
    private long tablaNotasId; // A qué tabla pertenece
    private double puntaje;
    private double nota;

    public ItemTabla() {}

    public ItemTabla(double puntaje, double nota) {
        this.puntaje = puntaje;
        this.nota = nota;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getTablaNotasId() { return tablaNotasId; }
    public void setTablaNotasId(long tablaNotasId) { this.tablaNotasId = tablaNotasId; }
    public double getPuntaje() { return puntaje; }
    public void setPuntaje(double puntaje) { this.puntaje = puntaje; }
    public double getNota() { return nota; }
    public void setNota(double nota) { this.nota = nota; }
}