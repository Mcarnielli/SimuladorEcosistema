package simulador.modelo;

import simulador.interfaces.Peligroso;

/**
 * BONUS: planta venenosa. Se guarda en el mismo ArrayList<Planta> que las plantas
 * normales (polimorfismo), y el conejo no puede distinguirla antes de comerla.
 */
public class PlantaVenenosa extends Planta implements Peligroso {

    public static final double DANIO_VENENO = 30;

    public PlantaVenenosa(String nombre, double energia, int tamanio) {
        super(nombre, energia, tamanio);
    }

    @Override
    public String getTipo() {
        return "Planta Venenosa";
    }

    /** En vez de alimentar, resta energía: devuelve un valor negativo. */
    @Override
    public double serComida() {
        super.serComida(); // igual se "consume" (queda sin energía y muere)
        return -DANIO_VENENO;
    }

    @Override
    public int getNivelPeligro() {
        return 3;
    }

    @Override
    public void mostrarEstado() {
        super.mostrarEstado();
        System.out.println("    (venenosa)");
    }
}
