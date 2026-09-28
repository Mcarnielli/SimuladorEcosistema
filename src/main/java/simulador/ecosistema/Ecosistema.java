package simulador.ecosistema;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import simulador.interfaces.Mortal;
import simulador.interfaces.Peligroso;
import simulador.interfaces.Reproducible;
import simulador.modelo.Clima;
import simulador.modelo.Conejo;
import simulador.modelo.Entidad;
import simulador.modelo.Lobo;
import simulador.modelo.Planta;
import simulador.modelo.PlantaVenenosa;

/**
 * Contiene todas las entidades, procesa los turnos y lleva las estadísticas.
 */
public class Ecosistema {

    public static final int MAX_LOBOS_TOTALES = 5;
    public static final double PROB_PLANTA_VENENOSA = 0.15;
    /** Capacidad del terreno: evita que las poblaciones crezcan sin límite. */
    public static final int CAPACIDAD_PLANTAS = 40;
    public static final int CAPACIDAD_CONEJOS = 30;

    private static final String PLANTAS = "Plantas";
    private static final String CONEJOS = "Conejos";
    private static final String LOBOS = "Lobos";

    private static final String[] ESPECIES_PLANTAS = {"Helecho", "Trebol", "Alfalfa", "Pasto", "Diente"};
    private static final String[] NOMBRES_CONEJOS = {"Blas", "Luna", "Topo", "Rex", "Copito", "Nube",
        "Pelusa", "Tambor", "Mora", "Chispa", "Bigotes", "Pompon", "Canela", "Oreo", "Algodon"};
    private static final String[] NOMBRES_LOBOS = {"Fang", "Sombra", "Colmillo", "Aullido", "Niebla"};

    // ---------- Atributos pedidos por la consigna ----------
    private ArrayList<Planta> plantas;
    private ArrayList<Conejo> conejos;
    private ArrayList<Lobo> lobos;
    private Clima climaActual;
    private int turnoActual;

    // ---------- Atributos de soporte ----------
    private final int turnosTotales;
    private final Random random;
    private final ArrayList<Entidad> todasLasEntidades;   // vivas y muertas, para el reporte
    private final Map<String, Integer> nacimientos;
    private final Map<String, Integer> muertes;
    private final Map<String, Integer> agregadosPorJugador;
    private final ArrayList<int[]> historialPoblacion;    // BONUS: [turno, plantas, conejos, lobos]
    private int eventosTurno;
    private int turnoMayorActividad;
    private int maxEventos;
    private int lobosCreados;
    private int contadorPlantas;
    private int contadorConejos;

    public Ecosistema(Clima climaInicial, int turnosTotales) {
        this.plantas = new ArrayList<>();
        this.conejos = new ArrayList<>();
        this.lobos = new ArrayList<>();
        this.climaActual = climaInicial;
        this.turnoActual = 0;
        this.turnosTotales = turnosTotales;
        this.random = new Random();
        this.todasLasEntidades = new ArrayList<>();
        this.nacimientos = crearContador();
        this.muertes = crearContador();
        this.agregadosPorJugador = crearContador();
        this.historialPoblacion = new ArrayList<>();
        this.turnoMayorActividad = 0;
        this.maxEventos = -1;
    }

    private Map<String, Integer> crearContador() {
        Map<String, Integer> m = new LinkedHashMap<>();
        m.put(PLANTAS, 0);
        m.put(CONEJOS, 0);
        m.put(LOBOS, 0);
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
        if (e instanceof Planta) {
            plantas.add((Planta) e);
        } else if (e instanceof Conejo) {
            conejos.add((Conejo) e);
        } else if (e instanceof Lobo) {
            lobos.add((Lobo) e);
        }
        todasLasEntidades.add(e);
    }

    private String claveDe(Entidad e) {
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

    /** Imprime un evento del turno y lo cuenta (para el "turno de mayor actividad"). */
    public void registrarEvento(String mensaje) {
        System.out.println("  " + mensaje);
        eventosTurno++;
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

    /** true si todavía hay lugar para que nazca otra planta. */
    public boolean hayEspacioParaPlantas() {
        return filtrarVivas(plantas).size() < CAPACIDAD_PLANTAS;
    }

    /** true si todavía hay lugar para que nazca otro conejo. */
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
    private ArrayList<Entidad> getEntidadesVivas() {
        ArrayList<Entidad> todas = new ArrayList<>();
        todas.addAll(filtrarVivas(plantas));
        todas.addAll(filtrarVivas(conejos));
        todas.addAll(filtrarVivas(lobos));
        return todas;
    }

    /** Plantas y conejos en la misma lista, tratados sólo como Reproducible. */
    private ArrayList<Reproducible> getReproducibles() {
        ArrayList<Reproducible> lista = new ArrayList<>();
        lista.addAll(filtrarVivas(plantas));
        lista.addAll(filtrarVivas(conejos));
        return lista;
    }

    /** Plantas, conejos y lobos tratados sólo como Mortal. */
    private ArrayList<Mortal> getMortales() {
        ArrayList<Mortal> lista = new ArrayList<>();
        lista.addAll(plantas);
        lista.addAll(conejos);
        lista.addAll(lobos);
        return lista;
    }

    // =====================================================================
    //  Turno
    // =====================================================================

    public void procesarTurno() {
        turnoActual++;
        eventosTurno = 0;

        System.out.println();
        System.out.println("=== TURNO " + turnoActual + " | Clima: " + climaActual + " ===");
        System.out.println(lineaConteo());
        System.out.println("-- Eventos --");

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
        // 5) Las entidades sin energía mueren (método default de Mortal)
        for (Mortal m : getMortales()) {
            if (m.verificarMuerte()) {
                eventosTurno++;
            }
        }
        limpiarMuertos();

        if (eventosTurno == 0) {
            System.out.println("  (sin eventos este turno)");
        }
        if (eventosTurno > maxEventos) {
            maxEventos = eventosTurno;
            turnoMayorActividad = turnoActual;
        }
        registrarHistorial();

        // 6) Estado del ecosistema
        System.out.println("Estado: " + lineaConteo());
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

    private String lineaConteo() {
        return "Plantas: " + plantas.size() + "  Conejos: " + conejos.size() + "  Lobos: " + lobos.size();
    }

    // =====================================================================
    //  Métodos pedidos por la consigna
    // =====================================================================

    public void mostrarEstado() {
        int aptos = 0;
        for (Reproducible r : getReproducibles()) { // plantas y conejos en un mismo recorrido
            if (r.puedeReproducirse()) {
                aptos++;
            }
        }
        System.out.println("--- Estado del ecosistema (turno " + turnoActual + ") ---");
        System.out.println(lineaConteo() + "  | Clima: " + climaActual);
        System.out.println("Entidades en condiciones de reproducirse: " + aptos);
    }

    /** Muestra el detalle de cada entidad viva (mostrarEstado polimórfico). */
    public void mostrarDetalle() {
        System.out.println("--- Detalle de entidades ---");
        for (Entidad e : getEntidadesVivas()) {
            e.mostrarEstado();
        }
    }

    /** Sobrecarga 1: energía inicial aleatoria. */
    public boolean agregarEntidad(String tipo) {
        return agregarEntidad(tipo, energiaAleatoria(tipo.toLowerCase().trim()));
    }

    /** Sobrecarga 2: energía inicial elegida por el jugador. */
    public boolean agregarEntidad(String tipo, double energiaInicial) {
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
                    System.out.println("No se pueden tener mas de " + MAX_LOBOS_TOTALES
                            + " lobos en total en toda la simulacion.");
                    return false;
                }
                nueva = crearLobo(energiaInicial);
                break;
            default:
                System.out.println("Tipo de entidad invalido: " + tipo);
                return false;
        }
        agregarALista(nueva);
        sumar(agregadosPorJugador, claveDe(nueva), 1);
        System.out.println("Se agrego '" + nueva.getNombre() + "' al ecosistema (energia: "
                + (int) nueva.getEnergia() + ").");
        return true;
    }

    /** Cuenta todos los lobos creados en la simulación (iniciales + agregados). */
    public boolean puedeAgregarLobo() {
        return lobosCreados < MAX_LOBOS_TOTALES;
    }

    public void cambiarClima(Clima nuevo) {
        Clima anterior = climaActual;
        climaActual = nuevo;
        System.out.println("El clima cambio de " + anterior + " a " + nuevo + ".");
    }

    public boolean ecosistemaColapsado() {
        return plantas.isEmpty() || conejos.isEmpty() || lobos.isEmpty();
    }

    private String poblacionesExtintas() {
        List<String> extintas = new ArrayList<>();
        if (plantas.isEmpty()) extintas.add(PLANTAS);
        if (conejos.isEmpty()) extintas.add(CONEJOS);
        if (lobos.isEmpty()) extintas.add(LOBOS);
        return String.join(", ", extintas);
    }

    // =====================================================================
    //  Reporte final
    // =====================================================================

    public void generarReporteFinal() {
        String linea = "==================================================";
        System.out.println();
        System.out.println(linea);
        System.out.println("              REPORTE FINAL DE LA SIMULACION");
        System.out.println(linea);

        // Causa de fin
        if (ecosistemaColapsado()) {
            System.out.println("Causa de fin: COLAPSO del ecosistema en el turno " + turnoActual
                    + ". Poblacion extinguida: " + poblacionesExtintas());
        } else {
            System.out.println("Causa de fin: se completaron los " + turnosTotales + " turnos configurados.");
        }
        System.out.println("Clima final: " + climaActual);
        System.out.println("Poblacion final -> " + lineaConteo());

        // Turno de mayor actividad
        System.out.println();
        System.out.println("Turno de mayor actividad: turno " + turnoMayorActividad
                + " (" + maxEventos + " eventos)");

        // Entidad más longeva de cada tipo
        System.out.println();
        System.out.println("Entidad mas longeva de cada tipo:");
        imprimirLongeva("Planta", Planta.class);
        imprimirLongeva("Conejo", Conejo.class);
        imprimirLongeva("Lobo", Lobo.class);

        // Lobo con más cacerías
        System.out.println();
        Lobo mejorCazador = null;
        for (Entidad e : todasLasEntidades) {
            if (e instanceof Lobo) {
                Lobo l = (Lobo) e;
                if (mejorCazador == null || l.getExitosCaza() > mejorCazador.getExitosCaza()) {
                    mejorCazador = l;
                }
            }
        }
        if (mejorCazador != null) {
            System.out.println("Lobo con mas cacerias exitosas: '" + mejorCazador.getNombre() + "' ("
                    + mejorCazador.getExitosCaza() + " cacerias)" + estadoTexto(mejorCazador));
        }

        // Nacimientos y muertes por tipo
        System.out.println();
        System.out.println("Nacimientos, muertes y agregados por tipo:");
        System.out.printf("  %-8s | %11s | %7s | %20s%n", "Tipo", "Nacimientos", "Muertes", "Agregados (jugador)");
        for (String tipo : nacimientos.keySet()) {
            System.out.printf("  %-8s | %11d | %7d | %20d%n", tipo,
                    nacimientos.get(tipo), muertes.get(tipo), agregadosPorJugador.get(tipo));
        }

        reporteEstadisticas();
        reportePeligrosos();
        System.out.println(linea);
    }

    private void imprimirLongeva(String etiqueta, Class<? extends Entidad> clase) {
        Entidad masVieja = null;
        for (Entidad e : todasLasEntidades) {
            if (clase.isInstance(e) && (masVieja == null || e.getEdad() > masVieja.getEdad())) {
                masVieja = e;
            }
        }
        if (masVieja == null) {
            System.out.println("  " + etiqueta + ": -");
        } else {
            System.out.println("  " + etiqueta + ": '" + masVieja.getNombre() + "' con "
                    + masVieja.getEdad() + " turnos" + estadoTexto(masVieja));
        }
    }

    private String estadoTexto(Entidad e) {
        return e.estaVivo() ? " (sigue con vida)" : " (murio)";
    }

    /** BONUS: máximo y mínimo de cada población a lo largo de la simulación. */
    private void reporteEstadisticas() {
        System.out.println();
        System.out.println("Estadisticas de poblacion (turno 0 = inicio):");
        String[] nombres = {PLANTAS, CONEJOS, LOBOS};
        for (int i = 0; i < nombres.length; i++) {
            int col = i + 1;
            int[] max = historialPoblacion.get(0);
            int[] min = historialPoblacion.get(0);
            for (int[] fila : historialPoblacion) {
                if (fila[col] > max[col]) max = fila;
                if (fila[col] < min[col]) min = fila;
            }
            System.out.printf("  %-8s -> maximo: %3d (turno %d) | minimo: %3d (turno %d)%n",
                    nombres[i], max[col], max[0], min[col], min[0]);
        }

        System.out.println("  Historial turno a turno:");
        System.out.println("    Turno | Plantas | Conejos | Lobos");
        for (int[] fila : historialPoblacion) {
            System.out.printf("    %5d | %7d | %7d | %5d%n", fila[0], fila[1], fila[2], fila[3]);
        }
    }

    /** BONUS: elementos peligrosos ordenados por nivel (de mayor a menor). */
    private void reportePeligrosos() {
        ArrayList<Peligroso> peligrosos = new ArrayList<>();
        for (Entidad e : todasLasEntidades) {
            if (e instanceof Peligroso) {
                peligrosos.add((Peligroso) e);
            }
        }
        peligrosos.sort(Comparator.comparingInt(Peligroso::getNivelPeligro).reversed());

        System.out.println();
        System.out.println("Elementos peligrosos del ecosistema (ordenados por nivel):");
        if (peligrosos.isEmpty()) {
            System.out.println("  (ninguno)");
        }
        for (Peligroso p : peligrosos) {
            System.out.println("  Nivel " + p.getNivelPeligro() + " - " + p.getDescripcion()
                    + (p.estaVivo() ? " (vivo)" : " (muerto)"));
        }
    }

    // ---------- Getters ----------

    public ArrayList<Planta> getPlantas() { return plantas; }
    public ArrayList<Conejo> getConejos() { return conejos; }
    public ArrayList<Lobo> getLobos() { return lobos; }
    public Clima getClimaActual() { return climaActual; }
    public int getTurnoActual() { return turnoActual; }
    public int getTurnosTotales() { return turnosTotales; }
    public Random getRandom() { return random; }
    public int getLobosCreados() { return lobosCreados; }
}
