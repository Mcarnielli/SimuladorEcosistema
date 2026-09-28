package simulador;

import java.util.Scanner;

/**
 * Punto de entrada del programa.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Simulador simulador = new Simulador(scanner);
        simulador.iniciar();
        scanner.close();
    }
}
