package simulador.modelo;

import simulador.ecosistema.Ecosistema;
import simulador.interfaces.Peligroso;

/**
 * Lobo: caza conejos. No implementa Reproducible (los lobos no se reproducen).
 */
public class Lobo extends Animal implements Peligroso {

    public static final double GANANCIA_CAZA = 30;
    public static final double PROB_MINIMA = 0.05;
    public static final double PROB_MAXIMA = 0.90;

    private int exitosCaza;

    public Lobo(String nombre, double energia, int velocidad, double peso) {
        super(nombre, energia, velocidad, peso);
        this.exitosCaza = 0;
    }

    @Override
    public String getTipo() {
        return "Lobo";
    }

    @Override
    protected double getGastoBase() {
        return 5;
    }

    @Override
    public void actuar(Ecosistema eco) {
        if (!estaVivo()) {
            return;
        }
        double efectoClima = eco.getClimaActual().getModEnergiaLobos();
        if (efectoClima != 0) {
            ganarEnergia(efectoClima); // en Lluvioso es -5
        }
        comer(eco);
    }

    /**
     * La probabilidad de éxito CRECE con la energía del lobo:
     * 0.05 + (energia/100) * 0.45  -> con 0 de energia 5%, con 50 ~27%, con 100 50%.
     * En Invierno se suma +20%. Se limita al rango [5%, 90%].
     */
    public double calcularProbabilidadCaza(Clima clima) {
        double prob = PROB_MINIMA + (getEnergia() / ENERGIA_MAXIMA) * 0.45 + clima.getBonusCaza();
        return Math.max(PROB_MINIMA, Math.min(PROB_MAXIMA, prob));
    }

    @Override
    public void comer(Ecosistema eco) {
        Conejo presa = eco.buscarConejoVivo();
        if (presa == null) {
            moverse();
            eco.registrarEvento(getDescripcion() + " no encontro conejos para cazar");
            return;
        }
        double prob = calcularProbabilidadCaza(eco.getClimaActual());
        if (eco.getRandom().nextDouble() < prob) {
            presa.morir();
            ganarEnergia(GANANCIA_CAZA);
            exitosCaza++;
            eco.registrarEvento(getDescripcion() + " cazo a " + presa.getDescripcion()
                    + " (+" + (int) GANANCIA_CAZA + " energia) [cacerias: " + exitosCaza + "]");
        } else {
            eco.registrarEvento(getDescripcion() + " fallo la caza (prob. de exito: "
                    + Math.round(prob * 100) + "%)");
        }
    }

    /** BONUS Peligroso: más cacerías y más energía = más peligroso. */
    @Override
    public int getNivelPeligro() {
        return 5 + exitosCaza * 2 + (int) (getEnergia() / 25);
    }

    @Override
    public void mostrarEstado() {
        System.out.printf("  %-16s | energia: %5.1f | edad: %d | cacerias exitosas: %d%n",
                getNombre(), getEnergia(), getEdad(), exitosCaza);
    }

    public int getExitosCaza() {
        return exitosCaza;
    }

    public void setExitosCaza(int exitosCaza) {
        this.exitosCaza = Math.max(0, exitosCaza);
    }
}
