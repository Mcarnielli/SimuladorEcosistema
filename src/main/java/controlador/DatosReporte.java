package controlador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Datos del reporte final ya convertidos en filas para las tablas de la vista.
 */
public class DatosReporte {

    private final String causaFin;
    private final List<Object[]> resumen;
    private final List<Object[]> poblaciones;
    private final List<Object[]> historial;
    private final List<Object[]> maxMin;
    private final List<Object[]> peligrosos;

    public DatosReporte(String causaFin, List<Object[]> resumen, List<Object[]> poblaciones,
            List<Object[]> historial, List<Object[]> maxMin, List<Object[]> peligrosos) {
        this.causaFin = causaFin;
        this.resumen = new ArrayList<>(resumen);
        this.poblaciones = new ArrayList<>(poblaciones);
        this.historial = new ArrayList<>(historial);
        this.maxMin = new ArrayList<>(maxMin);
        this.peligrosos = new ArrayList<>(peligrosos);
    }

    public String getCausaFin() { return causaFin; }
    public List<Object[]> getResumen() { return Collections.unmodifiableList(resumen); }
    public List<Object[]> getPoblaciones() { return Collections.unmodifiableList(poblaciones); }
    public List<Object[]> getHistorial() { return Collections.unmodifiableList(historial); }
    public List<Object[]> getMaxMin() { return Collections.unmodifiableList(maxMin); }
    public List<Object[]> getPeligrosos() { return Collections.unmodifiableList(peligrosos); }
}
