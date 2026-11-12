package controlador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Datos ya preparados para la pantalla de simulación.
 * La vista los muestra tal cual: no calcula ni decide nada.
 */
public class DatosSimulacion {

    private final String turno;
    private final String clima;
    private final int plantas;
    private final int conejos;
    private final int lobos;
    private final List<Object[]> filasEntidades;
    private final List<String> eventos;
    private final boolean puedeAvanzar;
    private final boolean puedeIntervenir;
    private final boolean puedeAgregarLobo;
    private final String estadoIntervencion;
    private final String mensajeEstado;

    public DatosSimulacion(String turno, String clima, int plantas, int conejos, int lobos,
            List<Object[]> filasEntidades, List<String> eventos, boolean puedeAvanzar,
            boolean puedeIntervenir, boolean puedeAgregarLobo, String estadoIntervencion,
            String mensajeEstado) {
        this.turno = turno;
        this.clima = clima;
        this.plantas = plantas;
        this.conejos = conejos;
        this.lobos = lobos;
        this.filasEntidades = new ArrayList<>(filasEntidades);
        this.eventos = new ArrayList<>(eventos);
        this.puedeAvanzar = puedeAvanzar;
        this.puedeIntervenir = puedeIntervenir;
        this.puedeAgregarLobo = puedeAgregarLobo;
        this.estadoIntervencion = estadoIntervencion;
        this.mensajeEstado = mensajeEstado;
    }

    public String getTurno() { return turno; }
    public String getClima() { return clima; }
    public int getPlantas() { return plantas; }
    public int getConejos() { return conejos; }
    public int getLobos() { return lobos; }
    public List<Object[]> getFilasEntidades() { return Collections.unmodifiableList(filasEntidades); }
    public List<String> getEventos() { return Collections.unmodifiableList(eventos); }
    public boolean isPuedeAvanzar() { return puedeAvanzar; }
    public boolean isPuedeIntervenir() { return puedeIntervenir; }
    public boolean isPuedeAgregarLobo() { return puedeAgregarLobo; }
    public String getEstadoIntervencion() { return estadoIntervencion; }
    public String getMensajeEstado() { return mensajeEstado; }
}
