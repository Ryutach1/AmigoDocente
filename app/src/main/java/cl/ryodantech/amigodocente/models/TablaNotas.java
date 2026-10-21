package cl.ryodantech.amigodocente.models;

import java.util.List;

public class TablaNotas {
    private long id;
    private long evaluacionId;
    private List<ItemTabla> items;

    public TablaNotas() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getEvaluacionId() { return evaluacionId; }
    public void setEvaluacionId(long evaluacionId) { this.evaluacionId = evaluacionId; }
    public List<ItemTabla> getItems() { return items; }
    public void setItems(List<ItemTabla> items) { this.items = items; }
}