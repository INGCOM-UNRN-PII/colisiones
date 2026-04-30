package ar.unrn.diccionario;

import java.util.Objects;

/**
 * Representa una clave diseñada específicamente para pruebas, permitiendo forzar
 * un valor de {@code hashCode} particular.
 * <p>
 * Esta clase es útil para simular colisiones de hash en estructuras de datos
 * como {@link java.util.HashMap} o implementaciones personalizadas de diccionarios,
 * ya que se puede controlar explícitamente el código hash devuelto,
 * independientemente del contenido del campo {@code name}.
 * </p>
 * <p>
 * La igualdad ({@link #equals(Object)}) entre instancias de {@code LlaveDefectuosa}
 * se basa únicamente en el valor de su campo {@code name}. El campo
 * {@code forcedHashCode} no participa en la determinación de la igualdad,
 * lo que permite crear objetos que son diferentes según {@code equals} pero
 * que tienen el mismo {@code hashCode}, o viceversa.
 * </p>
 */
public class LlaveDefectuosa {
    /**
     * El nombre o identificador de esta llave. Utilizado para la comparación
     * en el método {@link #equals(Object)} y para la representación en
     * {@link #toString()}.
     */
    private final String name;

    /**
     * El valor de código hash que esta instancia devolverá consistentemente
     * a través de su método {@link #hashCode()}.
     */
    private final int forcedHashCode;

    /**
     * Construye una nueva instancia de {@code LlaveDefectuosa}.
     *
     * @param name           El nombre para esta llave, usado en {@code equals} y {@code toString}.
     *                       No debería ser {@code null} para un comportamiento predecible
     *                       en {@code equals}.
     * @param forcedHashCode El valor entero que será devuelto por el método
     *                       {@link #hashCode()} de esta instancia.
     */
    public LlaveDefectuosa(String name, int forcedHashCode) {
        this.name = name;
        this.forcedHashCode = forcedHashCode;
    }

    /**
     * Devuelve un código hash forzado para este objeto.
     * <p>
     * Este método siempre devuelve el valor de {@code forcedHashCode} que se
     * proporcionó durante la construcción de la instancia, ignorando el
     * contenido del campo {@code name}.
     * </p>
     *
     * @return el valor de {@code forcedHashCode} especificado en el constructor.
     */
    @Override
    public int hashCode() {
        return forcedHashCode;
    }

    /**
     * Compara esta {@code LlaveDefectuosa} con otro objeto para determinar la igualdad.
     * <p>
     * Dos instancias de {@code LlaveDefectuosa} se consideran iguales si y solo si
     * el objeto especificado también es una instancia de {@code LlaveDefectuosa}
     * y sus campos {@code name} son iguales según {@link Objects#equals(Object, Object)}.
     * El campo {@code forcedHashCode} no se utiliza en esta comparación.
     * </p>
     * <p>
     * Esta implementación utiliza "pattern matching for instanceof" (Java 16+)
     * para un casteo más conciso y seguro, y ha sido refactorizada para tener
     * un único punto de retorno.
     * </p>
     *
     * @param objeto el objeto con el que se va a comparar.
     * @return {@code true} si el objeto especificado es igual a esta
     * {@code LlaveDefectuosa}; {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object objeto) {
        boolean sonIguales;
        if (this == objeto) {
            sonIguales = true;
        } else if (objeto instanceof LlaveDefectuosa otra) {
            sonIguales = Objects.equals(name, otra.name);
        } else {
            sonIguales = false;
        }
        return sonIguales;
    }

    /**
     * Devuelve una representación en forma de {@code String} de esta {@code LlaveDefectuosa}.
     * <p>
     * La representación es simplemente el valor del campo {@code name}.
     * </p>
     *
     * @return el campo {@code name} de esta llave.
     */
    @Override
    public String toString() {
        return name;
    }
}