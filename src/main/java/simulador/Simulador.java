package simulador;

import java.util.Scanner;

import simulador.ecosistema.Ecosistema;
import simulador.modelo.Clima;
import simulador.modelo.Entidad;

/**
 * Maneja la interacción con el jugador por consola:
 * configuración inicial, loop principal e intervenciones.
 */
public class Simulador {

    public static final int TURNOS_ENTRE_INTERVENCIONES = 3;

    private final Scanner scanner;
    private Ecosistema ecosistema;

    public Simulador(Scanner scanner) {
        this.scanner = scanner;
    }

    public void iniciar() {
        System.out.println("==================================================");
        System.out.println("          SIMULADOR DE ECOSISTEMA");
        System.out.println("==================================================");
        configurar();
        loopPrincipal();
        ecosistema.generarReporteFinal();
    }

    // =====================================================================
    //  1. Configuración inicial
    // =====================================================================

    private void configurar() {
        boolean confirmado = false;
        while (!confirmado) {
            System.out.println();
            System.out.println("--- Configuracion inicial ---");
            int plantas = leerEntero("Cantidad inicial de plantas (5-30): ", 5, 30);
            int conejos = leerEntero("Cantidad inicial de conejos (2-15): ", 2, 15);
            int lobos = leerEntero("Cantidad inicial de lobos (1-5): ", 1, 5);
            Clima clima = elegirClima();
            int turnos = leerEntero("Cantidad de turnos de la simulacion (10-50): ", 10, 50);

            System.out.println();
            System.out.println("Resumen de la configuracion:");
            System.out.println("  Plantas: " + plantas + " | Conejos: " + conejos + " | Lobos: " + lobos);
            System.out.println("  Clima inicial: " + clima + " | Turnos: " + turnos);

            if (confirmar("Confirmar e iniciar la simulacion?")) {
                ecosistema = new Ecosistema(clima, turnos);
                ecosistema.inicializar(plantas, conejos, lobos);
                confirmado = true;
            } else {
                System.out.println("Volvamos a cargar la configuracion.");
            }
        }
        System.out.println();
        ecosistema.mostrarEstado();
    }

    // =====================================================================
    //  2. Loop principal
    // =====================================================================

    private void loopPrincipal() {
        boolean fin = false;
        while (!fin) {
            ecosistema.procesarTurno();

            fin = ecosistema.getTurnoActual() >= ecosistema.getTurnosTotales()
                    || ecosistema.ecosistemaColapsado();

            pausar();

            if (!fin && ecosistema.getTurnoActual() % TURNOS_ENTRE_INTERVENCIONES == 0) {
                intervenir();
            }
        }
    }

    private void pausar() {
        System.out.print(">>> Presione Enter para continuar...");
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

    // =====================================================================
    //  3. Intervención del jugador
    // =====================================================================

    private void intervenir() {
        boolean terminado = false;
        while (!terminado) {
            System.out.println();
            System.out.println("=== INTERVENCION (cada " + TURNOS_ENTRE_INTERVENCIONES + " turnos) ===");
            System.out.println("1. Cambiar clima (actual: " + ecosistema.getClimaActual() + ")");
            System.out.println("2. Agregar entidad");
            System.out.println("3. Solo avanzar");
            System.out.println("4. Ver detalle de las entidades (no consume la intervencion)");
            int opcion = leerEntero("Opcion: ", 1, 4);

            switch (opcion) {
                case 1:
                    terminado = intervenirClima();
                    break;
                case 2:
                    terminado = intervenirAgregar();
                    break;
                case 3:
                    terminado = confirmar("Avanzar sin intervenir?");
                    break;
                case 4:
                    ecosistema.mostrarEstado();
                    ecosistema.mostrarDetalle();
                    break;
                default:
                    break;
            }
        }
    }

    private boolean intervenirClima() {
        Clima nuevo = elegirClima();
        if (nuevo == ecosistema.getClimaActual()) {
            System.out.println("Ese ya es el clima actual.");
            return false;
        }
        if (confirmar("Cambiar el clima a " + nuevo + "?")) {
            ecosistema.cambiarClima(nuevo);
            return true;
        }
        return false;
    }

    private boolean intervenirAgregar() {
        String tipo = leerTipoEntidad();
        if (tipo.equals("lobo") && !ecosistema.puedeAgregarLobo()) {
            System.out.println("No se puede: ya hubo " + Ecosistema.MAX_LOBOS_TOTALES
                    + " lobos en esta simulacion (maximo permitido).");
            return false;
        }

        boolean energiaManual = confirmar("Elegir la energia inicial? (si responde 'n' es aleatoria)");
        double energia = 0;
        if (energiaManual) {
            energia = leerEntero("Energia inicial (1-" + (int) Entidad.ENERGIA_MAXIMA + "): ",
                    1, (int) Entidad.ENERGIA_MAXIMA);
        }

        if (!confirmar("Agregar " + tipo + " al ecosistema?")) {
            return false;
        }
        // Uso de la sobrecarga de agregarEntidad
        if (energiaManual) {
            return ecosistema.agregarEntidad(tipo, energia);
        }
        return ecosistema.agregarEntidad(tipo);
    }

    // =====================================================================
    //  Lectura validada de datos
    // =====================================================================

    private int leerEntero(String mensaje, int min, int max) {
        while (true) {
            System.out.print(mensaje);
            String linea = leerLinea();
            try {
                int valor = Integer.parseInt(linea.trim());
                if (valor >= min && valor <= max) {
                    return valor;
                }
                System.out.println("  Valor fuera de rango. Debe estar entre " + min + " y " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  Ingrese un numero entero valido.");
            }
        }
    }

    private Clima elegirClima() {
        System.out.println("Climas disponibles:");
        Clima[] climas = Clima.values();
        for (int i = 0; i < climas.length; i++) {
            System.out.println("  " + (i + 1) + ". " + climas[i]);
        }
        int opcion = leerEntero("Elija el clima (1-" + climas.length + "): ", 1, climas.length);
        return Clima.desdeOpcion(opcion);
    }

    private String leerTipoEntidad() {
        while (true) {
            System.out.print("Que entidad agregar? (planta/conejo/lobo): ");
            String tipo = leerLinea().trim().toLowerCase();
            if (tipo.equals("planta") || tipo.equals("conejo") || tipo.equals("lobo")) {
                return tipo;
            }
            System.out.println("  Opcion invalida. Escriba planta, conejo o lobo.");
        }
    }

    private boolean confirmar(String pregunta) {
        while (true) {
            System.out.print(pregunta + " (s/n): ");
            String r = leerLinea().trim().toLowerCase();
            if (r.equals("s") || r.equals("si")) {
                return true;
            }
            if (r.equals("n") || r.equals("no")) {
                return false;
            }
            System.out.println("  Responda 's' o 'n'.");
        }
    }

    private String leerLinea() {
        if (!scanner.hasNextLine()) {
            System.out.println();
            System.out.println("Entrada finalizada. Saliendo del simulador.");
            System.exit(0);
        }
        return scanner.nextLine();
    }
}
