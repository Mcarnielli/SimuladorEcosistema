package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Datos del reporte final, ya calculados por el Ecosistema.
 * Es un objeto de datos: no imprime ni muestra nada. El controlador lo
 * convierte en filas y la vista lo muestra en tablas.
 */
public class ReporteFinal {

    private String causaFin;
    private String climaFinal;
    private int turnosJugados;
    private int turnoMayorActividad;
    private int eventosMayorActividad;
    private String mejorCazadorNombre;
    private int mejorCazadorCacerias;
    private boolean mejorCazadorVivo;

    /** [tipo, nombre, edad, estado] */
    private final List<String[]> longevas = new ArrayList<>();
    /** [tipo, iniciales, nacimientos, agregados, muertes, finales] */
    private final List<Object[]> conteos = new ArrayList<>();
    /** [turno, plantas, conejos, lobos] */
    private final List<int[]> historial = new ArrayList<>();
    /** [tipo, maximo, turnoMaximo, minimo, turnoMinimo] */
    private final List<Object[]> maxMin = new ArrayList<>();
    /** [nivel, descripcion, estado] */
    private final List<Object[]> peligrosos = new ArrayList<>();

    // ---------- Carga (la usa el Ecosistema) ----------

    void agregarLongeva(String tipo, Entidad e) {
        if (e == null) {
            longevas.add(new String[]{tipo, "-", "-", "-"});
        } else {
            longevas.add(new String[]{tipo, e.getNombre(), String.valueOf(e.getEdad()),
                e.estaVivo() ? "Sigue con vida" : "Murió"});
        }
    }

    void agregarConteo(String tipo, int iniciales, int nacimientos, int agregados, int muertes, int finales) {
        conteos.add(new Object[]{tipo, iniciales, nacimientos, agregados, muertes, finales});
    }

    void agregarHistorial(int[] fila) {
        historial.add(fila);
    }

    void agregarMaxMin(String tipo, int maximo, int turnoMax, int minimo, int turnoMin) {
        maxMin.add(new Object[]{tipo, maximo, turnoMax, minimo, turnoMin});
    }

    void agregarPeligroso(int nivel, String descripcion, boolean vivo) {
        peligrosos.add(new Object[]{nivel, descripcion, vivo ? "Vivo" : "Muerto"});
    }

    void setMejorCazador(String nombre, int cacerias, boolean vivo) {
        this.mejorCazadorNombre = nombre;
        this.mejorCazadorCacerias = cacerias;
        this.mejorCazadorVivo = vivo;
    }

    void setCausaFin(String causaFin) { this.causaFin = causaFin; }
    void setClimaFinal(String climaFinal) { this.climaFinal = climaFinal; }
    void setTurnosJugados(int turnosJugados) { this.turnosJugados = turnosJugados; }
    void setTurnoMayorActividad(int turno) { this.turnoMayorActividad = turno; }
    void setEventosMayorActividad(int eventos) { this.eventosMayorActividad = eventos; }

    // ---------- Getters ----------

    public String getCausaFin() { return causaFin; }
    public String getClimaFinal() { return climaFinal; }
    public int getTurnosJugados() { return turnosJugados; }
    public int getTurnoMayorActividad() { return turnoMayorActividad; }
    public int getEventosMayorActividad() { return eventosMayorActividad; }
    public String getMejorCazadorNombre() { return mejorCazadorNombre; }
    public int getMejorCazadorCacerias() { return mejorCazadorCacerias; }
    public boolean isMejorCazadorVivo() { return mejorCazadorVivo; }
    public List<String[]> getLongevas() { return Collections.unmodifiableList(longevas); }
    public List<Object[]> getConteos() { return Collections.unmodifiableList(conteos); }
    public List<int[]> getHistorial() { return Collections.unmodifiableList(historial); }
    public List<Object[]> getMaxMin() { return Collections.unmodifiableList(maxMin); }
    public List<Object[]> getPeligrosos() { return Collections.unmodifiableList(peligrosos); }
}
