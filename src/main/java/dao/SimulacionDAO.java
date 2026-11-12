package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import modelo.Clima;
import modelo.Conejo;
import modelo.Ecosistema;
import modelo.Entidad;
import modelo.Lobo;
import modelo.Planta;
import modelo.PlantaVenenosa;

/**
 * Guarda y lee la simulación en un archivo de texto plano.
 *
 * El archivo está dentro de la carpeta del proyecto (ruta relativa "datos/simulacion.txt").
 * Cada línea empieza con un tipo de registro y los campos se separan con "|":
 *
 * <pre>
 * SIMULADOR_ECOSISTEMA|1
 * GENERAL|turnosTotales|turnoActual|CLIMA|lobosCreados|contadorPlantas|contadorConejos|turnoMayorActividad|maxEventos|intervencionRealizada
 * INICIALES|plantas|conejos|lobos        (igual para NACIMIENTOS, MUERTES y AGREGADOS)
 * HISTORIAL|turno|plantas|conejos|lobos
 * ENTIDAD|PLANTA|nombre|energia|edad|viva|tamanio
 * ENTIDAD|PLANTA_VENENOSA|nombre|energia|edad|viva|tamanio
 * ENTIDAD|CONEJO|nombre|energia|edad|viva|velocidad|peso
 * ENTIDAD|LOBO|nombre|energia|edad|viva|velocidad|peso|exitosCaza
 * EVENTO|texto del evento
 * FIN
 * </pre>
 *
 * No usa Swing ni conoce la vista o el controlador.
 */
public class SimulacionDAO {

    public static final String RUTA_POR_DEFECTO = "datos/simulacion.txt";

    private static final String SEP = "|";
    private static final String SEP_REGEX = "\\|";
    private static final String CABECERA = "SIMULADOR_ECOSISTEMA";
    private static final String VERSION = "1";

    private final Path archivo;

    public SimulacionDAO() {
        this(Paths.get(RUTA_POR_DEFECTO));
    }

    public SimulacionDAO(Path archivo) {
        this.archivo = archivo;
    }

    public boolean existeGuardado() {
        return Files.isRegularFile(archivo);
    }

    public String getRuta() {
        return archivo.toString();
    }

    // =====================================================================
    //  Guardar
    // =====================================================================

    /** Guarda el estado completo. Devuelve true si pudo guardar. */
    public boolean guardar(Ecosistema eco) {
        if (eco == null) {
            return false;
        }
        Path temporal = archivo.resolveSibling(archivo.getFileName() + ".tmp");
        try {
            Path carpeta = archivo.toAbsolutePath().getParent();
            if (carpeta != null) {
                Files.createDirectories(carpeta);
            }
            // Se escribe primero en un archivo temporal y después se reemplaza el real:
            // así, si algo falla a mitad de camino, no se pierde el guardado anterior.
            try (BufferedWriter w = Files.newBufferedWriter(temporal, StandardCharsets.UTF_8)) {
                escribir(w, CABECERA, VERSION);
                escribir(w, "GENERAL", eco.getTurnosTotales(), eco.getTurnoActual(), eco.getClimaActual().name(),
                        eco.getLobosCreados(), eco.getContadorPlantas(), eco.getContadorConejos(),
                        eco.getTurnoMayorActividad(), eco.getMaxEventos(), eco.isIntervencionRealizada());
                escribirContador(w, "INICIALES", eco.getIniciales());
                escribirContador(w, "NACIMIENTOS", eco.getNacimientos());
                escribirContador(w, "MUERTES", eco.getMuertes());
                escribirContador(w, "AGREGADOS", eco.getAgregadosPorJugador());
                for (int[] fila : eco.getHistorialPoblacion()) {
                    escribir(w, "HISTORIAL", fila[0], fila[1], fila[2], fila[3]);
                }
                for (Entidad e : eco.getTodasLasEntidades()) {
                    escribirEntidad(w, e);
                }
                for (String evento : eco.getEventosTurno()) {
                    escribir(w, "EVENTO", evento.replace('\n', ' '));
                }
                escribir(w, "FIN");
            }
            Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException | RuntimeException ex) {
            try {
                Files.deleteIfExists(temporal);
            } catch (IOException ignorada) {
                // no se puede hacer nada más
            }
            return false;
        }
    }

    private void escribirContador(BufferedWriter w, String tipo, java.util.Map<String, Integer> contador)
            throws IOException {
        escribir(w, tipo, contador.get(Ecosistema.PLANTAS), contador.get(Ecosistema.CONEJOS),
                contador.get(Ecosistema.LOBOS));
    }

    private void escribirEntidad(BufferedWriter w, Entidad e) throws IOException {
        String energia = formatear(e.getEnergia());
        if (e instanceof Planta) {
            String tipo = (e instanceof PlantaVenenosa) ? "PLANTA_VENENOSA" : "PLANTA";
            escribir(w, "ENTIDAD", tipo, e.getNombre(), energia, e.getEdad(), e.estaVivo(),
                    ((Planta) e).getTamanio());
        } else if (e instanceof Conejo) {
            Conejo c = (Conejo) e;
            escribir(w, "ENTIDAD", "CONEJO", c.getNombre(), energia, c.getEdad(), c.estaVivo(),
                    c.getVelocidad(), formatear(c.getPeso()));
        } else if (e instanceof Lobo) {
            Lobo l = (Lobo) e;
            escribir(w, "ENTIDAD", "LOBO", l.getNombre(), energia, l.getEdad(), l.estaVivo(),
                    l.getVelocidad(), formatear(l.getPeso()), l.getExitosCaza());
        }
    }

    private void escribir(BufferedWriter w, Object... campos) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) {
                sb.append(SEP);
            }
            sb.append(campos[i]);
        }
        w.write(sb.toString());
        w.newLine();
    }

    private String formatear(double valor) {
        return String.format(Locale.ROOT, "%.2f", valor);
    }

    // =====================================================================
    //  Cargar
    // =====================================================================

    /**
     * Lee el archivo y arma de nuevo el Ecosistema.
     * Devuelve null si no hay nada guardado.
     *
     * @throws DatosInvalidosException si el archivo no se puede leer o tiene datos inválidos
     */
    public Ecosistema cargar() throws DatosInvalidosException {
        if (!existeGuardado()) {
            return null;
        }
        List<String> lineas = new ArrayList<>();
        try (BufferedReader r = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = r.readLine()) != null) {
                lineas.add(linea);
            }
        } catch (IOException ex) {
            throw new DatosInvalidosException("No se pudo leer el archivo " + archivo + ".", ex);
        }
        try {
            return interpretar(lineas);
        } catch (DatosInvalidosException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new DatosInvalidosException("El archivo " + archivo + " tiene datos inválidos.", ex);
        }
    }

    private Ecosistema interpretar(List<String> lineas) throws DatosInvalidosException {
        if (lineas.isEmpty() || !lineas.get(0).equals(CABECERA + SEP + VERSION)) {
            throw new DatosInvalidosException("El archivo no tiene el formato esperado (falta la cabecera).");
        }
        String[] general = null;
        int[] iniciales = null, nacimientos = null, muertes = null, agregados = null;
        List<int[]> historial = new ArrayList<>();
        List<Entidad> entidades = new ArrayList<>();
        List<String> eventos = new ArrayList<>();
        boolean fin = false;

        for (int n = 1; n < lineas.size(); n++) {
            String linea = lineas.get(n);
            if (linea.trim().isEmpty()) {
                continue;
            }
            String tipo = linea.split(SEP_REGEX, 2)[0];
            String[] campos = linea.split(SEP_REGEX, -1);
            try {
                switch (tipo) {
                    case "GENERAL":
                        exigirCampos(campos, 10);
                        general = campos;
                        break;
                    case "INICIALES":
                        iniciales = leerContador(campos);
                        break;
                    case "NACIMIENTOS":
                        nacimientos = leerContador(campos);
                        break;
                    case "MUERTES":
                        muertes = leerContador(campos);
                        break;
                    case "AGREGADOS":
                        agregados = leerContador(campos);
                        break;
                    case "HISTORIAL":
                        exigirCampos(campos, 5);
                        historial.add(new int[]{entero(campos[1]), entero(campos[2]),
                            entero(campos[3]), entero(campos[4])});
                        break;
                    case "ENTIDAD":
                        entidades.add(leerEntidad(campos));
                        break;
                    case "EVENTO":
                        eventos.add(linea.split(SEP_REGEX, 2).length > 1 ? linea.split(SEP_REGEX, 2)[1] : "");
                        break;
                    case "FIN":
                        fin = true;
                        break;
                    default:
                        throw new DatosInvalidosException("Registro desconocido: " + tipo);
                }
            } catch (DatosInvalidosException | RuntimeException ex) {
                throw new DatosInvalidosException("Error en la línea " + (n + 1) + " del archivo: "
                        + ex.getMessage(), ex);
            }
        }

        if (!fin) {
            throw new DatosInvalidosException("El archivo está incompleto (falta la marca FIN).");
        }
        if (general == null || iniciales == null || nacimientos == null || muertes == null
                || agregados == null || historial.isEmpty()) {
            throw new DatosInvalidosException("Faltan datos obligatorios en el archivo.");
        }

        int turnosTotales = entero(general[1]);
        int turnoActual = entero(general[2]);
        Clima clima = Clima.valueOf(general[3]);
        if (turnosTotales < 10 || turnosTotales > 50 || turnoActual < 0 || turnoActual > turnosTotales) {
            throw new DatosInvalidosException("Los turnos guardados están fuera de rango.");
        }

        Ecosistema eco = new Ecosistema(clima, turnosTotales);
        eco.restaurarEstado(turnoActual, entero(general[4]), entero(general[5]), entero(general[6]),
                entero(general[7]), entero(general[8]), logico(general[9]),
                iniciales, nacimientos, muertes, agregados, historial, entidades, eventos);
        return eco;
    }

    private Entidad leerEntidad(String[] c) throws DatosInvalidosException {
        exigirCampos(c, 7);
        String tipo = c[1];
        String nombre = c[2];
        double energia = decimal(c[3]);
        int edad = entero(c[4]);
        boolean viva = logico(c[5]);
        Entidad e;
        switch (tipo) {
            case "PLANTA":
                e = new Planta(nombre, energia, entero(c[6]));
                break;
            case "PLANTA_VENENOSA":
                e = new PlantaVenenosa(nombre, energia, entero(c[6]));
                break;
            case "CONEJO":
                exigirCampos(c, 8);
                e = new Conejo(nombre, energia, entero(c[6]), decimal(c[7]));
                break;
            case "LOBO":
                exigirCampos(c, 9);
                Lobo l = new Lobo(nombre, energia, entero(c[6]), decimal(c[7]));
                l.setExitosCaza(entero(c[8]));
                e = l;
                break;
            default:
                throw new DatosInvalidosException("Tipo de entidad desconocido: " + tipo);
        }
        e.setEdad(edad);
        e.setViva(viva);
        return e;
    }

    private int[] leerContador(String[] c) throws DatosInvalidosException {
        exigirCampos(c, 4);
        return new int[]{entero(c[1]), entero(c[2]), entero(c[3])};
    }

    private void exigirCampos(String[] campos, int cantidad) throws DatosInvalidosException {
        if (campos.length < cantidad) {
            throw new DatosInvalidosException("faltan campos en el registro " + campos[0]);
        }
    }

    private int entero(String texto) {
        int valor = Integer.parseInt(texto.trim());
        if (valor < 0) {
            throw new NumberFormatException("valor negativo: " + texto);
        }
        return valor;
    }

    private double decimal(String texto) {
        return Double.parseDouble(texto.trim());
    }

    private boolean logico(String texto) throws DatosInvalidosException {
        if ("true".equals(texto)) return true;
        if ("false".equals(texto)) return false;
        throw new DatosInvalidosException("valor lógico inválido: " + texto);
    }

    // =====================================================================
    //  Eliminar
    // =====================================================================

    /** Borra el archivo guardado. Devuelve true si no queda ningún archivo. */
    public boolean eliminar() {
        try {
            Files.deleteIfExists(archivo);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
}
