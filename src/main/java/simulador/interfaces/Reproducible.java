package simulador.interfaces;

import simulador.ecosistema.Ecosistema;

/**
 * Comportamiento de las entidades que pueden reproducirse (Planta y Conejo).
 */
public interface Reproducible {

    void reproducirse(Ecosistema eco);

    boolean puedeReproducirse();

    /**
     * Método default: sólo se reproduce si cumple las condiciones.
     * Devuelve true si estaba en condiciones de intentarlo.
     */
    default boolean intentarReproduccion(Ecosistema eco) {
        if (puedeReproducirse()) {
            reproducirse(eco);
            return true;
        }
        return false;
    }
}
