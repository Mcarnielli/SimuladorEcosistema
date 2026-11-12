package modelo;


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
            perderEnergia(PERDIDA_SIN_COMIDA);
            String msg = moverse() + " y no encontró comida (-" + (int) PERDIDA_SIN_COMIDA + " energía)";
            if (enPeligro()) {
                msg += " [PELIGRO: energía=" + (int) getEnergia() + "]";
            }
            eco.registrarEvento(msg);
            return;
        }

        double valor = planta.serComida(); // polimorfismo: PlantaVenenosa devuelve negativo
        ganarEnergia(valor);
        if (valor >= 0) {
            eco.registrarEvento(getDescripcion() + " comió '" + planta.getNombre()
                    + "' (+" + (int) valor + " energía)");
        } else {
            String msg = getDescripcion() + " comió '" + planta.getNombre()
                    + "'... ¡era VENENOSA! (" + (int) valor + " energía)";
            if (enPeligro()) {
                msg += " [PELIGRO: energía=" + (int) getEnergia() + "]";
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
            eco.registrarEvento(getDescripcion() + " tuvo una cría -> nuevo conejo '"
                    + cria.getNombre() + "' (energía: " + (int) cria.getEnergia() + ")");
        }
    }

    public boolean enPeligro() {
        return getEnergia() < UMBRAL_PELIGRO;
    }

    @Override
    public String mostrarEstado() {
        return enPeligro() ? "EN PELIGRO (energía baja)" : (tieneHambre() ? "Con hambre" : "Satisfecho");
    }
}
