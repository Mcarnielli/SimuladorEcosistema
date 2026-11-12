package dao;

/**
 * Se lanza cuando el archivo guardado existe pero no se puede leer
 * o tiene datos inválidos. El DAO no muestra mensajes: informa el problema
 * y el controlador decide qué hacer.
 */
public class DatosInvalidosException extends Exception {

    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }

    public DatosInvalidosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
