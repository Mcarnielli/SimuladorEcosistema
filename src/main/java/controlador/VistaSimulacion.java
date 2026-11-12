package controlador;

import java.util.List;

/**
 * Lo que el controlador necesita de la vista.
 *
 * El controlador sólo conoce esta interfaz (no conoce Swing). La ventana
 * de la vista la implementa. Así el controlador puede ordenar "mostrá esto"
 * sin saber cómo se dibuja.
 */
public interface VistaSimulacion {

    void setControlador(ControladorSimulacion controlador);

    /** Carga las opciones de los combos (climas y tipos de entidad). */
    void cargarOpciones(List<String> climas, List<String> tiposEntidad);

    void mostrarPantallaConfiguracion(boolean puedeVolverASimulacion);

    void mostrarErroresConfiguracion(List<String> errores);

    void mostrarPantallaSimulacion();

    void actualizarSimulacion(DatosSimulacion datos);

    void mostrarPantallaReporte(DatosReporte datos);

    /** Habilita o deshabilita las opciones para ver el reporte. */
    void setReporteDisponible(boolean disponible);

    void mostrarMensaje(String titulo, String mensaje);

    void mostrarError(String mensaje);

    /** Pide confirmación a la persona. Devuelve true si acepta. */
    boolean confirmar(String titulo, String mensaje);
}
