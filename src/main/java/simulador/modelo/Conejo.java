package simulador.modelo;

import simulador.ecosistema.Ecosistema;
import simulador.interfaces.Reproducible;

/**
 * Conejo: come plantas y se reproduce si tiene energía y hay otro conejo vivo.
 */
public class Conejo extends Animal implements Reproducible {

    public static final double PERDIDA_SIN_COMIDA = 15;
    public static final double UMBRAL_REPRODUCCION = 60;
    public static final double COSTO_REPRODUCCION = 25;
    public static final double ENERGIA_CRIA = 35;
    public static final double PROB_REPRODUCCION = 0.3;
    public static final double UMBRAL_PELIGRO = 20;
    public static final double UMBRAL_HAMBRE = 50;

    public Conejo(String nombre, double energia, int velocidad, double peso) {
        super(nombre, energia, velocidad, peso);
    }

    @Override
    public String getTipo() {
        return "Conejo";
    }

    @Override
    protected double getGastoBase() {
        return 4;
    }

    /** Aplica el efecto del clima, come y luego intenta reproducirse. */
    @Override
    public void actuar(Ecosistema eco) {
        if (!estaVivo()) {
            return;
        }
        ganarEnergia(eco.getClimaActual().getModEnergiaConejos()); // puede ser negativo
        comer(eco);
        intentarReproduccion(eco);
    }

    /** Sólo come si tiene hambre; si está satisfecho no gasta plantas. */
    public boolean tieneHambre() {
        return getEnergia() < UMBRAL_HAMBRE;
    }

    @Override
    public void comer(Ecosistema eco) {
        if (!tieneHambre()) {
            return;
        }
        Planta planta = eco.buscarPlantaViva(); // no sabe si es venenosa
        if (planta == null) {
            moverse();
            perderEnergia(PERDIDA_SIN_COMIDA);
            String msg = getDescripcion() + " no encontro comida (-" + (int) PERDIDA_SIN_COMIDA + " energia)";
            if (enPeligro()) {
                msg += " [PELIGRO: energia=" + (int) getEnergia() + "]";
            }
            eco.registrarEvento(msg);
            return;
        }

        double valor = planta.serComida(); // polimorfismo: PlantaVenenosa devuelve negativo
        ganarEnergia(valor);
        if (valor >= 0) {
            eco.registrarEvento(getDescripcion() + " comio '" + planta.getNombre()
                    + "' (+" + (int) valor + " energia)");
        } else {
            String msg = getDescripcion() + " comio '" + planta.getNombre()
                    + "'... era VENENOSA! (" + (int) valor + " energia)";
            if (enPeligro()) {
                msg += " [PELIGRO: energia=" + (int) getEnergia() + "]";
            }
            eco.registrarEvento(msg);
        }
    }

    @Override
    public boolean puedeReproducirse() {
        return estaVivo() && getEnergia() > UMBRAL_REPRODUCCION;
    }

    /** Necesita al menos otro conejo vivo; aun así no siempre tiene cría. */
    @Override
    public void reproducirse(Ecosistema eco) {
        if (eco.contarConejosVivos() < 2 || !eco.hayEspacioParaConejos()) {
            return;
        }
        if (eco.getRandom().nextDouble() < PROB_REPRODUCCION) {
            perderEnergia(COSTO_REPRODUCCION);
            Conejo cria = eco.crearConejo(ENERGIA_CRIA);
            eco.registrarNacimiento(cria);
            eco.registrarEvento(getDescripcion() + " tuvo una cria -> nuevo conejo '"
                    + cria.getNombre() + "' (energia: " + (int) cria.getEnergia() + ")");
        }
    }

    public boolean enPeligro() {
        return getEnergia() < UMBRAL_PELIGRO;
    }

    @Override
    public void mostrarEstado() {
        System.out.printf("  %-16s | energia: %5.1f | edad: %d%s%n",
                getNombre(), getEnergia(), getEdad(), enPeligro() ? " | EN PELIGRO" : "");
    }
}
