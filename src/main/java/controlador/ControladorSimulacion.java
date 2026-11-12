package controlador;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dao.DatosInvalidosException;
import dao.SimulacionDAO;
import modelo.Clima;
import modelo.Ecosistema;
import modelo.Entidad;
import modelo.ReporteFinal;

/**
 * Recibe las acciones de la vista y coordina al modelo y al DAO.
 *
 * - Valida lo que ingresa la persona (campos vacíos, formato y rangos).
 * - Le pide al modelo que aplique las reglas y al DAO que guarde.
 * - Le entrega a la vista los datos ya preparados para mostrar.
 *
 * No tiene código Swing: sólo conoce la interfaz {@link VistaSimulacion}.
 */
public class ControladorSimulacion {

    public static final String[] TIPOS_ENTIDAD = {"Planta", "Conejo", "Lobo"};

    private final VistaSimulacion vista;
    private final SimulacionDAO dao;
    private Ecosistema ecosistema;

    public ControladorSimulacion(VistaSimulacion vista, SimulacionDAO dao) {
        this.vista = vista;
        this.dao = dao;
        this.vista.setControlador(this);
    }

    // =====================================================================
    //  Inicio de la aplicación: retomar lo guardado si existe
    // =====================================================================

    public void iniciar() {
        List<String> climas = new ArrayList<>();
        for (Clima c : Clima.values()) {
            climas.add(c.getNombre());
        }
        vista.cargarOpciones(climas, Arrays.asList(TIPOS_ENTIDAD));
        vista.setReporteDisponible(false);

        if (!dao.existeGuardado()) {
            vista.mostrarPantallaConfiguracion(false);
            return;
        }
        try {
            ecosistema = dao.cargar();
        } catch (DatosInvalidosException ex) {
            ecosistema = null;
            vista.mostrarPantallaConfiguracion(false);
            vista.mostrarError("No se pudo retomar la simulación guardada.\n" + ex.getMessage()
                    + "\n\nPodés configurar una simulación nueva.");
            return;
        }
        if (ecosistema == null) {
            vista.mostrarPantallaConfiguracion(false);
            return;
        }
        if (ecosistema.isFinalizada()) {
            vista.setReporteDisponible(true);
            actualizarVista("Se cargó una simulación ya terminada.");
            vista.mostrarPantallaReporte(armarReporte());
            vista.mostrarMensaje("Simulación guardada",
                    "La última simulación guardada ya había terminado. Se muestra su reporte final.");
        } else {
            actualizarVista("Se retomó la simulación guardada en el turno " + ecosistema.getTurnoActual() + ".");
            vista.mostrarPantallaSimulacion();
        }
    }

    // =====================================================================
    //  Configuración
    // =====================================================================

    /** El botón "Nueva simulación" (o el menú) lleva a la pantalla de configuración. */
    public void nuevaSimulacion() {
        if (hayEnCurso()) {
            boolean ok = vista.confirmar("Nueva simulación",
                    "Hay una simulación en curso (turno " + ecosistema.getTurnoActual() + " de "
                    + ecosistema.getTurnosTotales() + ").\n"
                    + "Si iniciás una nueva, la actual se va a reemplazar.\n\n¿Querés configurar una nueva?");
            if (!ok) {
                return;
            }
        }
        vista.mostrarPantallaConfiguracion(ecosistema != null);
    }

    /** Vuelve de la configuración a la simulación que ya estaba (si hay). */
    public void cancelarConfiguracion() {
        verSimulacion();
    }

    /** Valida los datos del formulario y, si están bien, crea la simulación. */
    public void iniciarSimulacion(String textoPlantas, String textoConejos, String textoLobos,
            String nombreClima, String textoTurnos) {
        List<String> errores = new ArrayList<>();
        Integer plantas = validarEntero(textoPlantas, "Plantas", 5, 30, errores);
        Integer conejos = validarEntero(textoConejos, "Conejos", 2, 15, errores);
        Integer lobos = validarEntero(textoLobos, "Lobos", 1, 5, errores);
        Clima clima = Clima.desdeNombre(nombreClima == null ? "" : nombreClima);
        if (clima == null) {
            errores.add("Clima: elegí un clima de la lista.");
        }
        Integer turnos = validarEntero(textoTurnos, "Turnos", 10, 50, errores);

        vista.mostrarErroresConfiguracion(errores);
        if (!errores.isEmpty()) {
            return;
        }

        String resumen = "Plantas: " + plantas + "\nConejos: " + conejos + "\nLobos: " + lobos
                + "\nClima inicial: " + clima + "\nTurnos: " + turnos;
        if (ecosistema != null) {
            resumen += "\n\nSe va a reemplazar la simulación guardada.";
        }
        if (!vista.confirmar("Confirmar configuración", resumen + "\n\n¿Iniciar la simulación?")) {
            return;
        }

        ecosistema = new Ecosistema(clima, turnos);
        ecosistema.inicializar(plantas, conejos, lobos);
        vista.setReporteDisponible(false);
        guardarYAvisar();
        actualizarVista("Simulación creada. Presioná «Avanzar turno» para empezar.");
        vista.mostrarPantallaSimulacion();
    }

    /**
     * Valida un número entero: no vacío, con formato correcto y dentro del rango.
     * Si hay un error lo agrega a la lista y devuelve null.
     */
    private Integer validarEntero(String texto, String campo, int min, int max, List<String> errores) {
        if (texto == null || texto.trim().isEmpty()) {
            errores.add(campo + ": el campo es obligatorio.");
            return null;
        }
        int valor;
        try {
            valor = Integer.parseInt(texto.trim());
        } catch (NumberFormatException ex) {
            errores.add(campo + ": tiene que ser un número entero (ingresaste \"" + texto.trim() + "\").");
            return null;
        }
        if (valor < min || valor > max) {
            errores.add(campo + ": tiene que estar entre " + min + " y " + max + ".");
            return null;
        }
        return valor;
    }

    // =====================================================================
    //  Simulación
    // =====================================================================

    public void avanzarTurno() {
        if (ecosistema == null) {
            vista.mostrarError("Todavía no hay una simulación en curso. Configurá una para empezar.");
            return;
        }
        if (ecosistema.isFinalizada()) {
            vista.mostrarError("La simulación ya terminó. Podés ver el reporte final o empezar una nueva.");
            return;
        }
        List<String> eventos = ecosistema.procesarTurno();
        guardarYAvisar();
        actualizarVista("Turno " + ecosistema.getTurnoActual() + " procesado: " + eventos.size() + " eventos.");

        if (ecosistema.isFinalizada()) {
            vista.setReporteDisponible(true);
            ReporteFinal reporte = ecosistema.generarReporteFinal();
            vista.mostrarMensaje("Fin de la simulación", reporte.getCausaFin()
                    + "\n\nA continuación se muestra el reporte final.");
            vista.mostrarPantallaReporte(armarReporte());
        }
    }

    public void cambiarClima(String nombreClima) {
        if (!validarIntervencion()) {
            return;
        }
        Clima nuevo = Clima.desdeNombre(nombreClima == null ? "" : nombreClima);
        if (nuevo == null) {
            vista.mostrarError("Elegí un clima de la lista.");
            return;
        }
        if (nuevo == ecosistema.getClimaActual()) {
            vista.mostrarError("El clima actual ya es " + nuevo + ". Elegí otro.");
            return;
        }
        if (!vista.confirmar("Cambiar clima", "¿Cambiar el clima de " + ecosistema.getClimaActual()
                + " a " + nuevo + "?\nEsta es la intervención de este turno.")) {
            return;
        }
        if (ecosistema.cambiarClima(nuevo)) {
            guardarYAvisar();
            actualizarVista("El clima cambió a " + nuevo + ".");
        } else {
            vista.mostrarError("No se pudo cambiar el clima.");
        }
    }

    public void agregarEntidad(String tipo, String textoEnergia) {
        if (!validarIntervencion()) {
            return;
        }
        if (tipo == null || tipo.trim().isEmpty()) {
            vista.mostrarError("Elegí qué entidad agregar.");
            return;
        }
        if (tipo.equalsIgnoreCase("Lobo") && !ecosistema.puedeAgregarLobo()) {
            vista.mostrarError("No se puede agregar otro lobo: ya hubo " + Ecosistema.MAX_LOBOS_TOTALES
                    + " lobos en esta simulación (es el máximo permitido).");
            return;
        }
        Integer energia = null;
        if (textoEnergia != null && !textoEnergia.trim().isEmpty()) {
            List<String> errores = new ArrayList<>();
            energia = validarEntero(textoEnergia, "Energía", 1, (int) Entidad.ENERGIA_MAXIMA, errores);
            if (energia == null) {
                vista.mostrarError(errores.get(0));
                return;
            }
        }
        String detalle = energia == null ? "con energía aleatoria" : "con energía " + energia;
        if (!vista.confirmar("Agregar entidad", "¿Agregar un/a " + tipo.toLowerCase() + " " + detalle
                + "?\nEsta es la intervención de este turno.")) {
            return;
        }
        // Sobrecarga de agregarEntidad: con o sin energía inicial
        Entidad nueva = (energia == null)
                ? ecosistema.agregarEntidad(tipo)
                : ecosistema.agregarEntidad(tipo, energia);
        if (nueva == null) {
            vista.mostrarError("No se pudo agregar la entidad.");
            return;
        }
        guardarYAvisar();
        actualizarVista("Se agregó " + nueva.getDescripcion() + ".");
    }

    private boolean validarIntervencion() {
        if (ecosistema == null) {
            vista.mostrarError("Todavía no hay una simulación en curso.");
            return false;
        }
        if (!ecosistema.puedeIntervenir()) {
            vista.mostrarError("Ahora no se puede intervenir. Se puede una vez cada "
                    + Ecosistema.TURNOS_ENTRE_INTERVENCIONES + " turnos.");
            return false;
        }
        return true;
    }

    public void verSimulacion() {
        if (ecosistema == null) {
            vista.mostrarError("Todavía no hay una simulación. Configurá una para empezar.");
            vista.mostrarPantallaConfiguracion(false);
            return;
        }
        actualizarVista(null);
        vista.mostrarPantallaSimulacion();
    }

    public void verReporte() {
        if (ecosistema == null) {
            vista.mostrarError("Todavía no hay una simulación, así que no hay reporte para mostrar.");
            return;
        }
        if (!ecosistema.isFinalizada()) {
            vista.mostrarError("El reporte final está disponible cuando termina la simulación (vas por el turno "
                    + ecosistema.getTurnoActual() + " de " + ecosistema.getTurnosTotales() + ").");
            return;
        }
        vista.mostrarPantallaReporte(armarReporte());
    }

    // =====================================================================
    //  Auxiliares: guardar y preparar los datos para la vista
    // =====================================================================

    private void guardarYAvisar() {
        if (!dao.guardar(ecosistema)) {
            vista.mostrarError("No se pudo guardar la simulación en " + dao.getRuta()
                    + ".\nPodés seguir jugando, pero si cerrás la aplicación se pierde el avance.");
        }
    }

    /** Arma los datos de la pantalla de simulación y se los pasa a la vista. */
    private void actualizarVista(String mensajeEstado) {
        List<Object[]> filas = new ArrayList<>();
        for (Entidad e : ecosistema.getEntidadesVivas()) {
            filas.add(new Object[]{e.getTipo(), e.getNombre(), (int) Math.round(e.getEnergia()),
                e.getEdad(), e.mostrarEstado()});
        }

        String estadoIntervencion;
        int proximo = ecosistema.getProximoTurnoIntervencion();
        if (ecosistema.isFinalizada()) {
            estadoIntervencion = "La simulación terminó. Podés ver el reporte final.";
        } else if (ecosistema.puedeIntervenir()) {
            estadoIntervencion = "¡Podés intervenir! Cambiá el clima o agregá una entidad (una sola acción), "
                    + "o simplemente avanzá el turno.";
        } else if (proximo > ecosistema.getTurnosTotales()) {
            estadoIntervencion = "No quedan más intervenciones en esta simulación.";
        } else if (ecosistema.isIntervencionRealizada()) {
            estadoIntervencion = "Ya intervino en este turno. La próxima intervención es en el turno " + proximo + ".";
        } else {
            estadoIntervencion = "La intervención se habilita cada " + Ecosistema.TURNOS_ENTRE_INTERVENCIONES
                    + " turnos. Próxima: turno " + proximo + ".";
        }

        String turno = ecosistema.getTurnoActual() + " / " + ecosistema.getTurnosTotales();
        String mensaje = mensajeEstado != null ? mensajeEstado : ecosistema.mostrarEstado();

        vista.actualizarSimulacion(new DatosSimulacion(turno, ecosistema.getClimaActual().getNombre(),
                ecosistema.getPlantas().size(), ecosistema.getConejos().size(), ecosistema.getLobos().size(),
                filas, ecosistema.getEventosTurno(), !ecosistema.isFinalizada(), ecosistema.puedeIntervenir(),
                ecosistema.puedeAgregarLobo(), estadoIntervencion, mensaje));
    }

    /** Convierte el ReporteFinal del modelo en filas para las tablas del reporte. */
    private DatosReporte armarReporte() {
        ReporteFinal r = ecosistema.generarReporteFinal();

        List<Object[]> resumen = new ArrayList<>();
        resumen.add(new Object[]{"Causa de fin", r.getCausaFin()});
        resumen.add(new Object[]{"Turnos jugados", r.getTurnosJugados()});
        resumen.add(new Object[]{"Clima final", r.getClimaFinal()});
        resumen.add(new Object[]{"Turno de mayor actividad",
            "Turno " + r.getTurnoMayorActividad() + " (" + r.getEventosMayorActividad() + " eventos)"});
        for (String[] l : r.getLongevas()) {
            String valor = "-".equals(l[1]) ? "-" : l[1] + " (" + l[2] + " turnos, " + l[3].toLowerCase() + ")";
            resumen.add(new Object[]{"Más longevo/a – " + l[0], valor});
        }
        if (r.getMejorCazadorNombre() != null) {
            resumen.add(new Object[]{"Lobo con más cacerías", r.getMejorCazadorNombre() + " ("
                + r.getMejorCazadorCacerias() + " cacerías, " + (r.isMejorCazadorVivo() ? "sigue con vida" : "murió") + ")"});
        }

        List<Object[]> historial = new ArrayList<>();
        for (int[] fila : r.getHistorial()) {
            historial.add(new Object[]{fila[0], fila[1], fila[2], fila[3]});
        }

        return new DatosReporte(r.getCausaFin(), resumen, r.getConteos(), historial,
                r.getMaxMin(), r.getPeligrosos());
    }

    private boolean hayEnCurso() {
        return ecosistema != null && !ecosistema.isFinalizada();
    }
}
