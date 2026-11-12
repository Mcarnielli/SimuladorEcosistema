package modelo;

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

    /** Texto del evento cuando la entidad muere sin energía. Planta lo sobreescribe. */
    default String getMensajeMuerte() {
        return getDescripcion() + " murió de inanición";
    }

    /**
     * Método default: si la entidad está viva pero se quedó sin energía, muere.
     * Devuelve true si murió en esta verificación (el Ecosistema registra el evento).
     */
    default boolean verificarMuerte() {
        if (estaVivo() && getEnergia() <= 0) {
            morir();
            return true;
        }
        return false;
    }
}
