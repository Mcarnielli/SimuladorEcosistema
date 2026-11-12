package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Contiene todas las entidades, aplica las reglas de cada turno y lleva las estadísticas.
 *
 * IE2: el modelo ya no imprime nada. Los eventos de cada turno se guardan en una lista
 * que el controlador pide para mostrarlos en la vista, y el reporte final se devuelve
 * como un objeto {@link ReporteFinal}.
 */
public class Ecosistema {

    public static final int MAX_LOBOS_TOTALES = 5;
    public static final int TURNOS_ENTRE_INTERVENCIONES = 3;
    public static final double PROB_PLANTA_VENENOSA = 0.15;
    /** Capacidad del terreno: evita que las poblaciones crezcan sin límite. */
    public static final int CAPACIDAD_PLANTAS = 40;
    public static final int CAPACIDAD_CONEJOS = 30;

    /** Claves de los contadores por tipo. */
    public static final String PLANTAS = "Plantas";
    public static final String CONEJOS = "Conejos";
    public static final String LOBOS = "Lobos";
    public static final String[] TIPOS = {PLANTAS, CONEJOS, LOBOS};

    private static final String[] ESPECIES_PLANTAS = {"Helecho", "Trebol", "Alfalfa", "Pasto", "Diente"};
    private static final String[] NOMBRES_CONEJOS = {"Blas", "Luna", "Topo", "Rex", "Copito", "Nube",
        "Pelusa", "Tambor", "Mora", "Chispa", "Bigotes", "Pompon", "Canela", "Oreo", "Algodon"};
    private static final String[] NOMBRES_LOBOS = {"Fang", "Sombra", "Colmillo", "Aullido", "Niebla"};

    // ---------- Atributos pedidos por la consigna ----------
    private final ArrayList<Planta> plantas;
    private final ArrayList<Conejo> conejos;
    private final ArrayList<Lobo> lobos;
    private Clima climaActual;
    private int turnoActual;

    // ---------- Atributos de soporte ----------
    private final int turnosTotales;
    private final Random random;
    private final ArrayList<Entidad> todasLasEntidades;   // vivas y muertas, para el reporte
    private final Map<String, Integer> iniciales;
    private final Map<String, Integer> nacimientos;
    private final Map<String, Integer> muertes;
    private final Map<String, Integer> agregadosPorJugador;
    private final ArrayList<int[]> historialPoblacion;    // BONUS: [turno, plantas, conejos, lobos]
    private final ArrayList<String> eventosTurno;         // eventos del último turno
    private int turnoMayorActividad;
    private int maxEventos;
    private int lobosCreados;
    private int contadorPlantas;
    private int contadorConejos;
    private boolean intervencionRealizada;

    public Ecosistema(Clima climaInicial, int turnosTotales) {
        this.plantas = new ArrayList<>();
        this.conejos = new ArrayList<>();
        this.lobos = new ArrayList<>();
        this.climaActual = climaInicial;
        this.turnoActual = 0;
        this.turnosTotales = turnosTotales;
        this.random = new Random();
        this.todasLasEntidades = new ArrayList<>();
        this.iniciales = crearContador();
        this.nacimientos = crearContador();
        this.muertes = crearContador();
        this.agregadosPorJugador = crearContador();
        this.historialPoblacion = new ArrayList<>();
        this.eventosTurno = new ArrayList<>();
        this.turnoMayorActividad = 0;
        this.maxEventos = 0;
        this.intervencionRealizada = false;
    }

    private static Map<String, Integer> crearContador() {
        Map<String, Integer> m = new LinkedHashMap<>();
        for (String tipo : TIPOS) {
            m.put(tipo, 0);
        }
        return m;
    }

    /** Crea las entidades iniciales con energía aleatoria. */
    public void inicializar(int cantPlantas, int cantConejos, int cantLobos) {
        for (int i = 0; i < cantPlantas; i++) {
            agregarALista(crearPlanta(energiaAleatoria("planta")));
        }
        for (int i = 0; i < cantConejos; i++) {
            agregarALista(crearConejo(energiaAleatoria("conejo")));
        }
        for (int i = 0; i < cantLobos; i++) {
            agregarALista(crearLobo(energiaAleatoria("lobo")));
        }
        iniciales.put(PLANTAS, cantPlantas);
        iniciales.put(CONEJOS, cantConejos);
        iniciales.put(LOBOS, cantLobos);
        eventosTurno.clear();
        eventosTurno.add("Se creó el ecosistema con " + cantPlantas + " plantas, " + cantConejos
                + " conejos y " + cantLobos + " lobos. Clima inicial: " + climaActual + ".");
        registrarHistorial(); // turno 0 = estado inicial
    }

    // =====================================================================
    //  Fábrica de entidades (sólo crean el objeto, no lo agregan)
    // =====================================================================

    /** Puede devolver una PlantaVenenosa: el resto del código la trata como Planta. */
    public Planta crearPlanta(double energia) {
        contadorPlantas++;
        String nombre = ESPECIES_PLANTAS[random.nextInt(ESPECIES_PLANTAS.length)] + "-" + contadorPlantas;
        int tamanio = 1 + random.nextInt(5);
        if (random.nextDouble() < PROB_PLANTA_VENENOSA) {
            return new PlantaVenenosa(nombre, energia, tamanio);
        }
        return new Planta(nombre, energia, tamanio);
    }

    public Conejo crearConejo(double energia) {
        String nombre = generarNombre(NOMBRES_CONEJOS, contadorConejos);
        contadorConejos++;
        return new Conejo(nombre, energia, 3 + random.nextInt(5), 1 + random.nextDouble() * 2);
    }

    public Lobo crearLobo(double energia) {
        String nombre = generarNombre(NOMBRES_LOBOS, lobosCreados);
        lobosCreados++;
        return new Lobo(nombre, energia, 5 + random.nextInt(5), 30 + random.nextDouble() * 20);
    }

    /** Si se terminan los nombres de la lista se agrega un número: "Blas-2". */
    private String generarNombre(String[] nombres, int indice) {
        String base = nombres[indice % nombres.length];
        int vuelta = indice / nombres.length;
        return vuelta == 0 ? base : base + "-" + (vuelta + 1);
    }

    private double energiaAleatoria(String tipo) {
        switch (tipo) {
            case "planta": return 30 + random.nextInt(31); // 30..60
            case "conejo": return 40 + random.nextInt(41); // 40..80
            default:       return 50 + random.nextInt(41); // 50..90
        }
    }

    private void agregarALista(Entidad e) {
        if (e.estaVivo()) {
            if (e instanceof Planta) {
                plantas.add((Planta) e);
            } else if (e instanceof Conejo) {
                conejos.add((Conejo) e);
            } else if (e instanceof Lobo) {
                lobos.add((Lobo) e);
            }
        }
        todasLasEntidades.add(e);
    }

    /** Devuelve la clave del contador ("Plantas", "Conejos" o "Lobos") de una entidad. */
    public static String claveDe(Entidad e) {
        if (e instanceof Planta) return PLANTAS;
        if (e instanceof Conejo) return CONEJOS;
        return LOBOS;
    }

    private void sumar(Map<String, Integer> contador, String clave, int cantidad) {
        contador.put(clave, contador.get(clave) + cantidad);
    }

    /** Lo llaman Planta y Conejo cuando se reproducen. */
    public void registrarNacimiento(Entidad nueva) {
        agregarALista(nueva);
        sumar(nacimientos, claveDe(nueva), 1);
    }

    /** Guarda un evento del turno (ya no se imprime: la vista lo pide por medio del controlador). */
    public void registrarEvento(String mensaje) {
        eventosTurno.add(mensaje);
    }

    // =====================================================================
    //  Búsquedas que usan las entidades
    // =====================================================================

    public Planta buscarPlantaViva() {
        List<Planta> vivas = filtrarVivas(plantas);
        return vivas.isEmpty() ? null : vivas.get(random.nextInt(vivas.size()));
    }

    public Conejo buscarConejoVivo() {
        List<Conejo> vivos = filtrarVivas(conejos);
        return vivos.isEmpty() ? null : vivos.get(random.nextInt(vivos.size()));
    }

    public boolean hayEspacioParaPlantas() {
        return filtrarVivas(plantas).size() < CAPACIDAD_PLANTAS;
    }

    public boolean hayEspacioParaConejos() {
        return filtrarVivas(conejos).size() < CAPACIDAD_CONEJOS;
    }

    public int contarConejosVivos() {
        return filtrarVivas(conejos).size();
    }

    private <T extends Entidad> List<T> filtrarVivas(List<T> lista) {
        List<T> vivas = new ArrayList<>();
        for (T e : lista) {
            if (e.estaVivo()) {
                vivas.add(e);
            }
        }
        return vivas;
    }

    /** Todas las entidades vivas en una sola lista (polimorfismo con Entidad). */
    public List<Entidad> getEntidadesVivas() {
        List<Entidad> todas = new ArrayList<>();
        todas.addAll(filtrarVivas(plantas));
        todas.addAll(filtrarVivas(conejos));
        todas.addAll(filtrarVivas(lobos));
        return todas;
    }

    /** Plantas y conejos en la misma lista, tratados sólo como Reproducible. */
    private List<Reproducible> getReproducibles() {
        List<Reproducible> lista = new ArrayList<>();
        lista.addAll(filtrarVivas(plantas));
        lista.addAll(filtrarVivas(conejos));
        return lista;
    }

    /** Plantas, conejos y lobos tratados sólo como Mortal. */
    private List<Mortal> getMortales() {
        List<Mortal> lista = new ArrayList<>();
        lista.addAll(plantas);
        lista.addAll(conejos);
        lista.addAll(lobos);
        return lista;
    }

    // =====================================================================
    //  Turno
    // =====================================================================

    /**
     * Procesa un turno completo y devuelve la lista de eventos que ocurrieron.
     * Si la simulación ya terminó no hace nada y devuelve una lista vacía.
     */
    public List<String> procesarTurno() {
        if (isFinalizada()) {
            return new ArrayList<>();
        }
        turnoActual++;
        eventosTurno.clear();
        intervencionRealizada = false;

        // 1) Plantas: fotosíntesis y reproducción. Se recorre una copia porque
        //    los brotes nuevos se agregan a la lista original durante el recorrido.
        for (Planta p : new ArrayList<>(plantas)) {
            p.actuar(this);
        }
        // 2) Conejos: comen y luego intentan reproducirse
        for (Conejo c : new ArrayList<>(conejos)) {
            c.actuar(this);
        }
        // 3) Lobos: intentan cazar
        for (Lobo l : new ArrayList<>(lobos)) {
            l.actuar(this);
        }
        // 4) Todas envejecen y gastan energía base (polimorfismo: getGastoBase() de cada una)
        for (Entidad e : getEntidadesVivas()) {
            e.envejecer();
        }
        // 5) Las entidades sin energía mueren (métodos default de Mortal)
        for (Mortal m : getMortales()) {
            if (m.verificarMuerte()) {
                registrarEvento(m.getMensajeMuerte());
            }
        }
        limpiarMuertos();

        if (eventosTurno.size() > maxEventos) {
            maxEventos = eventosTurno.size();
            turnoMayorActividad = turnoActual;
        }
        if (eventosTurno.isEmpty()) {
            eventosTurno.add("(sin eventos este turno)");
        }
        registrarHistorial();

        if (ecosistemaColapsado()) {
            eventosTurno.add("COLAPSO: se extinguieron " + poblacionesExtintas() + ".");
        } else if (turnoActual >= turnosTotales) {
            eventosTurno.add("Se completaron los " + turnosTotales + " turnos de la simulación.");
        }
        return getEventosTurno();
    }

    /** Saca de las listas a las entidades muertas y las cuenta por tipo. */
    private void limpiarMuertos() {
        int antes = plantas.size();
        plantas.removeIf(p -> !p.estaVivo());
        sumar(muertes, PLANTAS, antes - plantas.size());

        antes = conejos.size();
        conejos.removeIf(c -> !c.estaVivo());
        sumar(muertes, CONEJOS, antes - conejos.size());

        antes = lobos.size();
        lobos.removeIf(l -> !l.estaVivo());
        sumar(muertes, LOBOS, antes - lobos.size());
    }

    private void registrarHistorial() {
        historialPoblacion.add(new int[]{turnoActual, plantas.size(), conejos.size(), lobos.size()});
    }

    // =====================================================================
    //  Estado y reglas de intervención
    // =====================================================================

    /** Resumen corto del estado (antes se imprimía; ahora se devuelve como texto). */
    public String mostrarEstado() {
        int aptos = 0;
        for (Reproducible r : getReproducibles()) { // plantas y conejos en un mismo recorrido
            if (r.puedeReproducirse()) {
                aptos++;
            }
        }
        return "Turno " + turnoActual + " de " + turnosTotales + " | Clima: " + climaActual
                + " | Plantas: " + plantas.size() + " | Conejos: " + conejos.size()
                + " | Lobos: " + lobos.size() + " | Aptos para reproducirse: " + aptos;
    }

    public boolean ecosistemaColapsado() {
        return plantas.isEmpty() || conejos.isEmpty() || lobos.isEmpty();
    }

    /** La simulación termina por turnos completos o por colapso. */
    public boolean isFinalizada() {
        return turnoActual >= turnosTotales || ecosistemaColapsado();
    }

    /** Cada 3 turnos se puede hacer una intervención (cambiar clima o agregar una entidad). */
    public boolean puedeIntervenir() {
        return !isFinalizada()
                && turnoActual > 0
                && turnoActual % TURNOS_ENTRE_INTERVENCIONES == 0
                && !intervencionRealizada;
    }

    /** Turno en el que se habilita la próxima intervención. */
    public int getProximoTurnoIntervencion() {
        int proximo = (turnoActual / TURNOS_ENTRE_INTERVENCIONES + 1) * TURNOS_ENTRE_INTERVENCIONES;
        return proximo;
    }

    /** Cuenta todos los lobos creados en la simulación (iniciales + agregados). */
    public boolean puedeAgregarLobo() {
        return lobosCreados < MAX_LOBOS_TOTALES;
    }

    /** Cambia el clima como intervención. Devuelve false si no se puede intervenir. */
    public boolean cambiarClima(Clima nuevo) {
        if (!puedeIntervenir() || nuevo == null || nuevo == climaActual) {
            return false;
        }
        Clima anterior = climaActual;
        climaActual = nuevo;
        intervencionRealizada = true;
        registrarEvento("Intervención: el clima cambió de " + anterior + " a " + nuevo + ".");
        return true;
    }

    /** Sobrecarga 1: energía inicial aleatoria. Devuelve la entidad agregada o null. */
    public Entidad agregarEntidad(String tipo) {
        if (tipo == null) {
            return null;
        }
        return agregarEntidad(tipo, energiaAleatoria(tipo.toLowerCase().trim()));
    }

    /** Sobrecarga 2: energía inicial elegida por el jugador. Devuelve la entidad agregada o null. */
    public Entidad agregarEntidad(String tipo, double energiaInicial) {
        if (!puedeIntervenir() || tipo == null) {
            return null;
        }
        Entidad nueva;
        switch (tipo.toLowerCase().trim()) {
            case "planta":
                nueva = crearPlanta(energiaInicial);
                break;
            case "conejo":
                nueva = crearConejo(energiaInicial);
                break;
            case "lobo":
                if (!puedeAgregarLobo()) {
                    return null;
                }
                nueva = crearLobo(energiaInicial);
                break;
            default:
                return null;
        }
        agregarALista(nueva);
        sumar(agregadosPorJugador, claveDe(nueva), 1);
        intervencionRealizada = true;
        registrarEvento("Intervención: se agregó " + nueva.getDescripcion()
                + " (energía: " + (int) nueva.getEnergia() + ").");
        return nueva;
    }

    private String poblacionesExtintas() {
        List<String> extintas = new ArrayList<>();
        if (plantas.isEmpty()) extintas.add(PLANTAS);
        if (conejos.isEmpty()) extintas.add(CONEJOS);
        if (lobos.isEmpty()) extintas.add(LOBOS);
        return String.join(", ", extintas);
    }

    // =====================================================================
    //  Reporte final (el modelo calcula, la vista sólo muestra)
    // =====================================================================

    public ReporteFinal generarReporteFinal() {
        ReporteFinal r = new ReporteFinal();

        if (ecosistemaColapsado()) {
            r.setCausaFin("Colapso del ecosistema en el turno " + turnoActual
                    + ": se extinguieron " + poblacionesExtintas() + ".");
        } else if (turnoActual >= turnosTotales) {
            r.setCausaFin("Se completaron los " + turnosTotales + " turnos configurados.");
        } else {
            r.setCausaFin("La simulación todavía está en curso (turno " + turnoActual + " de " + turnosTotales + ").");
        }
        r.setClimaFinal(climaActual.getNombre());
        r.setTurnosJugados(turnoActual);
        r.setTurnoMayorActividad(turnoMayorActividad);
        r.setEventosMayorActividad(maxEventos);

        // Entidad más longeva de cada tipo (entre todas las que existieron)
        r.agregarLongeva(PLANTAS, buscarMasLongeva(Planta.class));
        r.agregarLongeva(CONEJOS, buscarMasLongeva(Conejo.class));
        r.agregarLongeva(LOBOS, buscarMasLongeva(Lobo.class));

        // Lobo con más cacerías
        Lobo mejor = null;
        for (Entidad e : todasLasEntidades) {
            if (e instanceof Lobo) {
                Lobo l = (Lobo) e;
                if (mejor == null || l.getExitosCaza() > mejor.getExitosCaza()) {
                    mejor = l;
                }
            }
        }
        if (mejor != null) {
            r.setMejorCazador(mejor.getNombre(), mejor.getExitosCaza(), mejor.estaVivo());
        }

        // Nacimientos, muertes, agregados y finales por tipo
        int[] finales = {plantas.size(), conejos.size(), lobos.size()};
        for (int i = 0; i < TIPOS.length; i++) {
            String t = TIPOS[i];
            r.agregarConteo(t, iniciales.get(t), nacimientos.get(t), agregadosPorJugador.get(t),
                    muertes.get(t), finales[i]);
        }

        // BONUS: historial y máximos/mínimos
        for (int[] fila : historialPoblacion) {
            r.agregarHistorial(fila.clone());
        }
        for (int i = 0; i < TIPOS.length; i++) {
            int col = i + 1;
            int[] max = historialPoblacion.get(0);
            int[] min = historialPoblacion.get(0);
            for (int[] fila : historialPoblacion) {
                if (fila[col] > max[col]) max = fila;
                if (fila[col] < min[col]) min = fila;
            }
            r.agregarMaxMin(TIPOS[i], max[col], max[0], min[col], min[0]);
        }

        // BONUS: peligrosos ordenados por nivel (de mayor a menor)
        List<Peligroso> peligrosos = new ArrayList<>();
        for (Entidad e : todasLasEntidades) {
            if (e instanceof Peligroso) {
                peligrosos.add((Peligroso) e);
            }
        }
        peligrosos.sort(Comparator.comparingInt(Peligroso::getNivelPeligro).reversed());
        for (Peligroso p : peligrosos) {
            r.agregarPeligroso(p.getNivelPeligro(), p.getDescripcion(), p.estaVivo());
        }
        return r;
    }

    private Entidad buscarMasLongeva(Class<? extends Entidad> clase) {
        Entidad masVieja = null;
        for (Entidad e : todasLasEntidades) {
            if (clase.isInstance(e) && (masVieja == null || e.getEdad() > masVieja.getEdad())) {
                masVieja = e;
            }
        }
        return masVieja;
    }

    // =====================================================================
    //  Restauración (la usa el DAO al cargar el archivo)
    // =====================================================================

    /**
     * Vuelve a armar el estado guardado. Las entidades se reciben todas juntas
     * (vivas y muertas); las vivas se vuelven a poner en su lista.
     */
    public void restaurarEstado(int turnoActual, int lobosCreados, int contadorPlantas, int contadorConejos,
            int turnoMayorActividad, int maxEventos, boolean intervencionRealizada,
            int[] iniciales, int[] nacimientos, int[] muertes, int[] agregados,
            List<int[]> historial, List<Entidad> entidades, List<String> eventos) {
        this.turnoActual = turnoActual;
        this.lobosCreados = lobosCreados;
        this.contadorPlantas = contadorPlantas;
        this.contadorConejos = contadorConejos;
        this.turnoMayorActividad = turnoMayorActividad;
        this.maxEventos = maxEventos;
        this.intervencionRealizada = intervencionRealizada;
        for (int i = 0; i < TIPOS.length; i++) {
            this.iniciales.put(TIPOS[i], iniciales[i]);
            this.nacimientos.put(TIPOS[i], nacimientos[i]);
            this.muertes.put(TIPOS[i], muertes[i]);
            this.agregadosPorJugador.put(TIPOS[i], agregados[i]);
        }
        plantas.clear();
        conejos.clear();
        lobos.clear();
        todasLasEntidades.clear();
        for (Entidad e : entidades) {
            agregarALista(e);
        }
        historialPoblacion.clear();
        historialPoblacion.addAll(historial);
        eventosTurno.clear();
        eventosTurno.addAll(eventos);
    }

    // ---------- Getters ----------

    public List<Planta> getPlantas() { return Collections.unmodifiableList(plantas); }
    public List<Conejo> getConejos() { return Collections.unmodifiableList(conejos); }
    public List<Lobo> getLobos() { return Collections.unmodifiableList(lobos); }
    public List<Entidad> getTodasLasEntidades() { return Collections.unmodifiableList(todasLasEntidades); }
    public List<String> getEventosTurno() { return new ArrayList<>(eventosTurno); }
    public List<int[]> getHistorialPoblacion() { return Collections.unmodifiableList(historialPoblacion); }
    public Map<String, Integer> getIniciales() { return Collections.unmodifiableMap(iniciales); }
    public Map<String, Integer> getNacimientos() { return Collections.unmodifiableMap(nacimientos); }
    public Map<String, Integer> getMuertes() { return Collections.unmodifiableMap(muertes); }
    public Map<String, Integer> getAgregadosPorJugador() { return Collections.unmodifiableMap(agregadosPorJugador); }
    public Clima getClimaActual() { return climaActual; }
    public int getTurnoActual() { return turnoActual; }
    public int getTurnosTotales() { return turnosTotales; }
    public Random getRandom() { return random; }
    public int getLobosCreados() { return lobosCreados; }
    public int getContadorPlantas() { return contadorPlantas; }
    public int getContadorConejos() { return contadorConejos; }
    public int getTurnoMayorActividad() { return turnoMayorActividad; }
    public int getMaxEventos() { return maxEventos; }
    public boolean isIntervencionRealizada() { return intervencionRealizada; }
}
