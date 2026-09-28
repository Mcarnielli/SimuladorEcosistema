package simulador.modelo;

import simulador.ecosistema.Ecosistema;
import simulador.interfaces.Mortal;

/**
 * Capa intermedia de herencia entre Entidad y los animales concretos.
 */
public abstract class Animal extends Entidad implements Mortal {

    private int velocidad;
    private double peso;

    public Animal(String nombre, double energia, int velocidad, double peso) {
        super(nombre, energia);
        setVelocidad(velocidad);
        setPeso(peso);
    }

    /** Cada animal come a su manera. */
    public abstract void comer(Ecosistema eco);

    /** Método concreto compartido por Conejo y Lobo. */
    public void moverse() {
        System.out.println("  " + getDescripcion() + " se desplazo buscando alimento (velocidad: " + velocidad + ")");
    }

    public int getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(int velocidad) {
        this.velocidad = Math.max(1, velocidad);
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso > 0 ? peso : 0.1;
    }
}
