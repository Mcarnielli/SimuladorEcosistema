package simulador.interfaces;

/**
 * BONUS: entidades peligrosas del ecosistema (Lobo y PlantaVenenosa).
 * Se usan en el reporte final ordenadas por nivel de peligro.
 */
public interface Peligroso {

    int getNivelPeligro();

    String getDescripcion();

    boolean estaVivo();
}
