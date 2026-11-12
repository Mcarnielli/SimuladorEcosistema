package modelo;

/**
 * Climas posibles del ecosistema y sus efectos sobre cada tipo de entidad.
 * Se implementan los 4 climas de la consigna (la IE2 pide como mínimo 3).
 */
public enum Clima {

    //        nombre      reproPlantas  energiaConejos  energiaLobos  bonusCaza
    SOLEADO ("Soleado",   1.5,          5,              0,            0.0),
    LLUVIOSO("Lluvioso",  2.0,          3,              -5,           0.0),
    SEQUIA  ("Sequía",    0.5,          -5,             0,            0.0),
    INVIERNO("Invierno",  0.0,          -8,             0,            0.20);

    private final String nombre;
    private final double multReproduccionPlantas;
    private final double modEnergiaConejos;
    private final double modEnergiaLobos;
    private final double bonusCaza;

    Clima(String nombre, double multReproduccionPlantas, double modEnergiaConejos,
          double modEnergiaLobos, double bonusCaza) {
        this.nombre = nombre;
        this.multReproduccionPlantas = multReproduccionPlantas;
        this.modEnergiaConejos = modEnergiaConejos;
        this.modEnergiaLobos = modEnergiaLobos;
        this.bonusCaza = bonusCaza;
    }

    public String getNombre() { return nombre; }
    public double getMultReproduccionPlantas() { return multReproduccionPlantas; }
    public double getModEnergiaConejos() { return modEnergiaConejos; }
    public double getModEnergiaLobos() { return modEnergiaLobos; }
    public double getBonusCaza() { return bonusCaza; }

    /** true si en este clima las plantas pueden reproducirse. */
    public boolean permiteReproduccionPlantas() {
        return multReproduccionPlantas > 0;
    }

    /** Busca un clima por su nombre visible ("Soleado", "Sequía"...). Devuelve null si no existe. */
    public static Clima desdeNombre(String nombre) {
        for (Clima c : values()) {
            if (c.nombre.equalsIgnoreCase(nombre) || c.name().equalsIgnoreCase(nombre)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
