package ar.unrn;

import java.util.Objects;

/**
 * Representa un objeto simple con un ID y un nombre.
 * <p>
 * Esta clase está diseñada para ser utilizada como clave en estructuras de datos
 * que dependen de las implementaciones de {@code equals} y {@code hashCode}.
 * Incluye un método fábrica estático, {@link #crearSiguiente()}, para generar
 * instancias con IDs secuenciales y nombres basados en una representación
 * en base 36.
 * </p>
 *
 * @author martinvilu
 * @version 2026.04.23
 * @contract.invar {@code id >= 0 && nombre != null && !nombre.isBlank()}
 */
public class ObjetoSimple {

    /**
     * Contador estático utilizado por el método fábrica {@link #crearSiguiente()}
     * para generar IDs únicos e incrementales para las nuevas instancias de
     * {@code ObjetoSimple}.
     * @contract.invar {@code contador >= 0}
     */
    private static int contador = 0;

    /**
     * El identificador numérico único para esta instancia de {@code ObjetoSimple}.
     * Es final y se asigna durante la construcción.
     * @contract.invar {@code id >= 0}
     */
    private final int id;

    /**
     * El nombre asociado con esta instancia de {@code ObjetoSimple}.
     * Es final y se asigna durante la construcción.
     * En el caso de objetos creados por {@link #crearSiguiente()}, este nombre
     * se genera como una representación en base 36 del ID.
     * @contract.invar {@code nombre != null && !nombre.isBlank()}
     */
    private final String nombre;

    /**
     * Construye una nueva instancia de {@code ObjetoSimple} con el ID y nombre especificados.
     *
     * @param id     El identificador único para este objeto.
     * @param nombre El nombre para este objeto.
     * @throws IllegalArgumentException si el nombre es nulo o vacío.
     * @contract.pre {@code id >= 0 && nombre != null && !nombre.isBlank()}
     * @contract.post {@code this.id == id && this.nombre.equals(nombre)}
     */
    public ObjetoSimple(int id, String nombre) {
        if (id < 0) {
            throw new IllegalArgumentException("El ID no puede ser negativo.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío.");
        }
        this.id = id;
        this.nombre = nombre;
    }

    /**
     * Método fábrica estático para crear la siguiente instancia secuencial de {@code ObjetoSimple}.
     * <p>
     * Incrementa un contador interno para asignar un nuevo ID único. El nombre del objeto
     * se genera convirtiendo este nuevo ID a una cadena en base 36 (utilizando caracteres 0-9 y A-Z).
     * </p>
     *
     * @return Una nueva instancia de {@code ObjetoSimple} con el siguiente ID
     *         secuencial y un nombre generado automáticamente.
     * @contract.post {@code result.id == old.contador + 1 && result.nombre != null}
     */
    public static ObjetoSimple crearSiguiente() {
        int siguienteId = ++contador; // Incrementa primero, luego asigna
        String nombreBase36 = Integer.toString(siguienteId, 36); // 0-9 y a-z
        String nombreGenerado = nombreBase36.toUpperCase(); // 0-9 y A-Z
        return new ObjetoSimple(siguienteId, nombreGenerado);
    }

    /**
     * Compara este {@code ObjetoSimple} con el objeto especificado para determinar la igualdad.
     * <p>
     * Dos instancias de {@code ObjetoSimple} se consideran iguales si y solo si
     * el objeto especificado también es una instancia de {@code ObjetoSimple}
     * y ambos tienen el mismo {@code id} y el mismo {@code nombre} (comparado usando
     * {@link Objects#equals(Object, Object)}).
     * </p>
     * <p>
     * Esta implementación utiliza "pattern matching for instanceof" (introducido en Java 16)
     * para un casteo más conciso y seguro, y sigue la práctica de tener un único
     * punto de retorno.
     * </p>
     *
     * @param objeto el objeto con el que se va a comparar.
     * @return {@code true} si el objeto especificado es igual a este
     *         {@code ObjetoSimple}; {@code false} en caso contrario.
     * @contract.pre {@code objeto != null}
     * @contract.post El resultado es {@code true} si {@code objeto} es un {@code ObjetoSimple} con el mismo {@code id} y {@code nombre}.
     */
    @Override
    public boolean equals(Object objeto) {
        boolean sonIguales;

        if (this == objeto) {
            sonIguales = true;
        } else if (objeto instanceof ObjetoSimple simple) {
            sonIguales = (id == simple.id && Objects.equals(nombre, simple.nombre));
        } else {
            sonIguales = false;
        }
        return sonIguales;
    }

    /**
     * Devuelve un código hash para este {@code ObjetoSimple}.
     * <p>
     * El código hash se calcula utilizando los campos {@code id} y {@code nombre},
     * combinados a través de {@link Objects#hash(Object...)}. Esto asegura que
     * dos objetos que son iguales según {@link #equals(Object)} producirán
     * el mismo código hash, cumpliendo el contrato general de {@code hashCode}.
     * </p>
     *
     * @return el código hash para este objeto.
     * @contract.post El resultado es un entero que representa el código hash del objeto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, nombre);
    }

    /**
     * Devuelve una representación en forma de cadena de este {@code ObjetoSimple}.
     * <p>
     * La cadena resultante incluye el nombre de la clase, el valor del {@code id},
     * el valor del {@code nombre}, y el {@code hashCode} del objeto.
     * Es útil principalmente para propósitos de depuración y registro.
     * </p>
     *
     * @return una representación en cadena de este objeto.
     * @contract.post El resultado es una cadena formateada con los atributos del objeto.
     */
    @Override
    public String toString() {
        return "ObjetoSimple{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", hashCode=" + this.hashCode() +
                '}';
    }
}
