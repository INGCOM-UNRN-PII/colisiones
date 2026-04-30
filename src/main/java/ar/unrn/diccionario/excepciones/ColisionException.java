package ar.unrn.diccionario.excepciones;

public class ColisionException extends DiccionarioException {
    public ColisionException(String mensaje) {
        super(mensaje);
    }
}
