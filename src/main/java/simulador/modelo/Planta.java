package simulador.modelo;

import simulador.ecosistema.Ecosistema;
import simulador.interfaces.Mortal;
import simulador.interfaces.Reproducible;

/**
 * Planta: gana energía por fotosíntesis y se reproduce según el clima.
 * Implementa Reproducible y (opcionalmente, según la consigna) Mortal.
 */
public class Planta extends Entidad implements Reproducible, Mortal {

    public static final double FOTOSINTESIS = 8;
    public static final double UMBRAL_REPRODUCCION = 40;
    public static final double COSTO_REPRODUCCION = 15;
    public static final double ENERGIA_BROTE = 30;
    public static final double PROB_BASE_REPRODUCCION = 0.5;

    private int tamanio; // 1..5

    public Planta(String nombre, double energia, int tamanio) {
        super(nombre, energia);
        setTamanio(tamanio);
    }

    @Override
    public String getTipo() {
        return "Planta";
    }

    @Override
    protected double getGastoBase() {
        return 3;
    }

    /** Hace fotosíntesis e intenta reproducirse si tiene energía y el clima lo permite. */
    @Override
    public void actuar(Ecosistema eco) {
        if (!estaVivo()) {
            return;
        }
        ganarEnergia(FOTOSINTESIS);
        intentarReproduccion(eco); // método default de Reproducible
    }

    @Override
    public boolean puedeReproducirse() {
        return estaVivo() && getEnergia() >= UMBRAL_REPRODUCCION;
    }

    /**
     * La probabilidad de reproducirse depende del clima:
     * Soleado x1.5, Lluvioso x2, Sequía x0.5, Invierno no se reproducen.
     */
    @Override
    public void reproducirse(Ecosistema eco) {
        Clima clima = eco.getClimaActual();
        if (!clima.permiteReproduccionPlantas() || !eco.hayEspacioParaPlantas()) {
            return;
        }
        double probabilidad = Math.min(1.0, PROB_BASE_REPRODUCCION * clima.getMultReproduccionPlantas());
        if (eco.getRandom().nextDouble() < probabilidad) {
            perderEnergia(COSTO_REPRODUCCION);
            Planta brote = eco.crearPlanta(ENERGIA_BROTE);
            eco.registrarNacimiento(brote);
            eco.registrarEvento(getDescripcion() + " se reprodujo -> nueva planta '"
                    + brote.getNombre() + "' (energia: " + (int) brote.getEnergia() + ")");
        }
    }

    /** Al ser comida la planta queda sin energía (muere) y devuelve su valor nutritivo. */
    public double serComida() {
        setEnergia(0);
        morir();
        return tamanio * 10;
    }

    /** Sobreescribe el default de Mortal para que el mensaje tenga sentido en una planta. */
    @Override
    public boolean verificarMuerte() {
        if (estaVivo() && getEnergia() <= 0) {
            morir();
            System.out.println("  " + getDescripcion() + " se seco (sin energia)");
            return true;
        }
        return false;
    }

    @Override
    public void mostrarEstado() {
        System.out.printf("  %-16s | tamanio: %d | energia: %5.1f | edad: %d%n",
                getNombre(), tamanio, getEnergia(), getEdad());
    }

    public int getTamanio() {
        return tamanio;
    }

    /** Validación: el tamaño siempre queda entre 1 y 5. */
    public void setTamanio(int tamanio) {
        this.tamanio = Math.max(1, Math.min(5, tamanio));
    }
}
