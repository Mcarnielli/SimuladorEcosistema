package simulador.interfaces;

/**
 * Comportamiento de las entidades que pueden morir (Conejo, Lobo y también Planta).
 * getEnergia() y getDescripcion() ya los implementa Entidad, así que
 * las subclases cumplen la interfaz sin escribir código extra.
 */
public interface Mortal {

    boolean estaVivo();

    void morir();

    double getEnergia();

    String getDescripcion();

    /**
     * Método default: si la entidad está viva pero se quedó sin energía, muere
     * y se imprime el evento. Devuelve true si murió en esta verificación.
     */
    default boolean verificarMuerte() {
        if (estaVivo() && getEnergia() <= 0) {
            morir();
            System.out.println("  " + getDescripcion() + " murio de inanicion");
            return true;
        }
        return false;
    }
}
