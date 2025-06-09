package ar.unrn.diccionario.excepciones;

public class DiccionarioException extends RuntimeException {
    public DiccionarioException(String mensaje) {
        super(mensaje);
    }
}
