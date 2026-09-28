package simulador.modelo;

import simulador.ecosistema.Ecosistema;

/**
 * Clase base abstracta de todas las entidades del ecosistema.
 * No se puede instanciar: sólo existen Plantas, Conejos y Lobos concretos.
 */
public abstract class Entidad {

    public static final double ENERGIA_MAXIMA = 100;

    private String nombre;
    private double energia;
    private int edad;
    private boolean viva;

    public Entidad(String nombre, double energia) {
        setNombre(nombre);
        setEnergia(energia);
        this.edad = 0;
        this.viva = true;
    }

    // ---------- Métodos abstractos (cada subclase los sobreescribe) ----------

    /** Lo que hace la entidad en su turno. */
    public abstract void actuar(Ecosistema eco);

    /** Imprime el estado de la entidad por consola. */
    public abstract void mostrarEstado();

    /** Nombre del tipo ("Planta", "Conejo", "Lobo"...). */
    public abstract String getTipo();

    // ---------- Métodos concretos ----------

    /**
     * Energía que la entidad gasta por el solo hecho de existir.
     * Cada subclase puede sobreescribirlo con su propio valor.
     */
    protected double getGastoBase() {
        return 5;
    }

    /** Incrementa la edad y descuenta la energía base. */
    public void envejecer() {
        if (!viva) {
            return;
        }
        edad++;
        setEnergia(energia - getGastoBase());
    }

    public void ganarEnergia(double cantidad) {
        setEnergia(energia + cantidad);
    }

    public void perderEnergia(double cantidad) {
        setEnergia(energia - cantidad);
    }

    public boolean estaVivo() {
        return viva;
    }

    public void morir() {
        this.viva = false;
    }

    /** Ej: "Conejo 'Blas'". */
    public String getDescripcion() {
        return getTipo() + " '" + nombre + "'";
    }

    // ---------- Getters y setters con validación ----------

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            this.nombre = "SinNombre";
        } else {
            this.nombre = nombre.trim();
        }
    }

    public double getEnergia() {
        return energia;
    }

    /** La energía nunca es negativa (se lleva a 0) ni supera el máximo. */
    public void setEnergia(double energia) {
        if (energia < 0) {
            this.energia = 0;
        } else if (energia > ENERGIA_MAXIMA) {
            this.energia = ENERGIA_MAXIMA;
        } else {
            this.energia = energia;
        }
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = Math.max(0, edad);
    }

    public boolean isViva() {
        return viva;
    }

    public void setViva(boolean viva) {
        this.viva = viva;
    }
}
