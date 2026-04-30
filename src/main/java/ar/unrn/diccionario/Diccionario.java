package ar.unrn.diccionario;

import ar.unrn.diccionario.excepciones.ColisionException;
import ar.unrn.diccionario.excepciones.LlaveNulaException;

import java.util.Arrays;
import java.util.Objects;

/**
 * Implementación de un diccionario simple (o mapa hash) con un número fijo de
 * "baldes" (buckets).
 * <p>
 * Este diccionario utiliza una estrategia de direccionamiento directo basada en el
 * {@code hashCode} de la clave. Las colisiones se manejan de una manera muy
 * simple: si un balde calculado para una nueva clave ya está ocupado por otra
 * entrada, la nueva inserción es rechazada. No implementa encadenamiento ni
 * sondeo lineal/cuadrático para resolver colisiones más allá de esta política
 * de rechazo.
 * </p>
 * <p>
 * Las claves utilizadas en este diccionario deben tener implementaciones
 * consistentes de {@code equals()} y {@code hashCode()}. Se recomienda que las
 * claves sean inmutables para evitar comportamientos inesperados si su estado
 * cambia después de ser insertadas.
 * </p>
 * <p>
 * Esta implementación no permite claves nulas, y lanzará una
 * {@link LlaveNulaException} si se intenta usar una.
 * </p>
 *
 * @param <K> el tipo de las claves mantenidas por este diccionario.
 * @param <V> el tipo de los valores mapeados.
 */
public class Diccionario<K, V> {

    /**
     * Número fijo de baldes (buckets) del diccionario.
     * Elegido como 256 (2^8) para permitir potencialmente el uso de operaciones
     * de bit (bitwise) si se cambiara la estrategia de cálculo de índice, aunque
     * actualmente se usa {@link Math#floorMod(int, int)}.
     */
    private static final int NUM_BUCKETS = 256; // 2^8 buckets
    /**
     * El array de baldes que almacena las entradas del diccionario.
     * Cada elemento del array puede contener una instancia de {@link Balde} o ser
     * {@code null} si el balde está vacío.
     */
    private final Balde<K, V>[] buckets;

    /**
     * Construye un nuevo diccionario vacío con un número predeterminado de baldes.
     * <p>
     * La creación del array de genéricos {@code Balde<K, V>[]} requiere un cast
     * desde {@code Balde[]} y suprime la advertencia "unchecked" debido a las
     * limitaciones de Java con arrays de tipos genéricos.
     * </p>
     */
    @SuppressWarnings("unchecked")
    public Diccionario() {
        buckets = (Balde<K, V>[]) new Balde[NUM_BUCKETS];
    }

    /**
     * Calcula el índice del balde para una clave dada.
     * <p>
     * Utiliza el {@code hashCode()} de la clave y aplica la operación de módulo
     * matemático ({@link Math#floorMod(int, int)}) para asegurar que el índice
     * resultante esté dentro del rango {@code [0, NUM_BUCKETS - 1]}.
     * Este método maneja correctamente tanto hashCodes positivos como negativos.
     * </p>
     * Nota: debiera de ser privado, pero lo dejamos público para buscar
     * colisiones divertidas
     *
     * @param key la clave para la que se desea calcular el índice del balde.
     *            No puede ser {@code null}.
     * @return el índice del balde, un valor entero entre 0 (inclusivo) y
     * {@code NUM_BUCKETS} (exclusivo).
     * @throws LlaveNulaException si la {@code key} proporcionada es {@code null}.
     */
    public int obtienePosicion(K key) {
        if (key == null) {
            throw new LlaveNulaException("La clave no puede ser nula.");
        }
        // El hashCode de la clave puede ser cualquier entero.
        // Queremos mapearlo a un valor entre 0 y NUM_BUCKETS - 1.
        // Math.floorMod(a, b) calcula el módulo de 'a' con respecto a 'b',
        // y el resultado siempre tiene el mismo signo que el divisor 'b'.
        // Dado que NUM_BUCKETS es positivo, el resultado estará en [0, NUM_BUCKETS - 1].
        // Esto maneja correctamente tanto hashCodes positivos como negativos.
        // https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Math.html#floorMod(int,int)
        return Math.floorMod(key.hashCode(), NUM_BUCKETS);
    }


    /**
     * Inserta una asociación de clave-valor en el diccionario.
     * <p>
     * Si el balde calculado para la clave especificada ya está ocupado por otra
     * entrada (independientemente de si la clave existente es la misma o diferente),
     * se lanza una {@link ColisionException}.
     * Si el balde está vacío, la nueva entrada se almacena y el método devuelve
     * {@code true}.
     * </p>
     *
     * @param key   la clave con la que se asociará el valor especificado.
     *              Debe ser inmutable o su comportamiento de {@code hashCode()} y
     *              {@code equals()} no debe cambiar mientras esté en el diccionario.
     *              No puede ser {@code null}.
     * @param value el valor que se asociará con la clave especificada.
     * @return {@code true} si la inserción fue exitosa (el balde estaba vacío).
     * @throws LlaveNulaException si la {@code key} proporcionada es {@code null}.
     * @throws ColisionException  si el balde calculado para la clave ya está ocupado.
     */
    public boolean put(K key, V value) {
        int index = obtienePosicion(key); // Puede lanzar LlaveNulaException

        if (buckets[index] == null) {
            buckets[index] = new Balde<>(key, value);
            return true; // Inserción exitosa
        } else {
            // El bucket ya está ocupado. Lanzar ColisionException.
            throw new ColisionException(
                    "Colisión en el bucket " + index + " al intentar insertar la clave: " + key +
                            ". El bucket ya está ocupado por la clave: " + buckets[index].llave);
        }
    }


    /**
     * Recupera el valor al que está mapeada la clave especificada, o {@code null}
     * si este diccionario no contiene ningún mapeo para la clave.
     * <p>
     * Un valor de retorno {@code null} no necesariamente indica que el diccionario
     * no contiene la clave; también podría significar que la clave fue mapeada
     * explícitamente a {@code null} (aunque esta implementación no distingue
     * esto de una clave ausente). Más precisamente, devuelve {@code null} si:
     * <ul>
     *   <li>El balde calculado para la clave está vacío.</li>
     *   <li>El balde calculado está ocupado, pero por una clave diferente
     *       (una colisión de hash que no fue resuelta con la clave buscada).</li>
     * </ul>
     * </p>
     *
     * @param key la clave cuyo valor asociado se va a devolver. No puede ser
     *            {@code null}.
     * @return el valor asociado a la clave especificada, o {@code null} si la clave
     * no se encuentra en su balde correspondiente o el balde está vacío.
     * @throws LlaveNulaException si la {@code key} proporcionada es {@code null}.
     */
    public V get(K key) {
        V valorRecuperado = null;
        int index = obtienePosicion(key);
        Balde<K, V> entry = buckets[index];

        if (entry != null && entry.llave.equals(key)) {
            // Importante: aunque el bucket esté ocupado, debemos asegurarnos de que
            // la clave sea la misma.
            // Esto es crucial para manejar el caso donde un hashCode diferente
            // (pero que mapea al mismo bucket) haya ocupado el espacio.
            valorRecuperado = entry.valor;
        }
        // Si la clave no se encuentra o el bucket está ocupado por otra entrada,
        // valorRecuperado permanece null.
        return valorRecuperado;
    }

    /**
     * Elimina el mapeo para una clave de este diccionario si está presente.
     * <p>
     * La eliminación ocurre si el balde calculado para la clave contiene una
     * entrada y la clave de esa entrada es igual (según {@code equals()}) a la
     * clave especificada.
     * </p>
     *
     * @param key la clave cuyo mapeo se va a eliminar del diccionario.
     *            No puede ser {@code null}.
     * @return {@code true} si la entrada fue eliminada exitosamente (es decir,
     * se encontró una entrada con la clave especificada y se eliminó),
     * {@code false} si la clave no fue encontrada en su balde
     * correspondiente o el balde estaba vacío.
     * @throws LlaveNulaException si la {@code key} proporcionada es {@code null}.
     */
    public boolean remove(K key) {
        boolean removidoConExito = false;
        int index = obtienePosicion(key);
        Balde<K, V> entry = buckets[index];

        if (entry != null && entry.llave.equals(key)) {
            buckets[index] = null; // Eliminar la entrada
            removidoConExito = true;
        }
        // Si la clave no se encuentra en este bucket o el bucket está vacío,
        // removidoConExito permanece false.
        return removidoConExito;
    }

    /**
     * Comprueba si este diccionario contiene un mapeo para la clave especificada.
     *
     * @param key la clave cuya presencia en este diccionario se va a comprobar.
     *            No puede ser {@code null}.
     * @return {@code true} si este diccionario contiene un mapeo para la clave
     * especificada (es decir, el balde correspondiente no está vacío y
     * contiene la misma clave), {@code false} en caso contrario.
     * @throws LlaveNulaException si la {@code key} proporcionada es {@code null}
     *                            (lanzada indirectamente por {@code getBucketIndex}).
     */
    public boolean containsKey(K key) {
        int index = obtienePosicion(key);
        Balde<K, V> entry = buckets[index];
        return entry != null && entry.llave.equals(key);
    }


    /**
     * Devuelve el número de asociaciones clave-valor en este diccionario.
     * <p>
     * Esto es equivalente al número de baldes que actualmente no son {@code null}
     * (es decir, que contienen un {@link Balde}).
     * </p>
     *
     * @return el número de elementos (asociaciones clave-valor) en este
     * diccionario.
     */
    public int size() {
        int baldes = 0;
        for (Balde<K, V> entry : buckets) {
            if (entry != null) {
                baldes++;
            }
        }
        return baldes;
    }


    /**
     * Comprueba si este diccionario no contiene ninguna asociación clave-valor.
     *
     * @return {@code true} si este diccionario está vacío (es decir, {@code size()}
     * es 0), {@code false} en caso contrario.
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public String toString() {
        return "Diccionario{" +
                "buckets=" + Arrays.toString(buckets) +
                '}';
    }

    /**
     * Clase interna estática que representa una entrada (un "balde" o bucket)
     * en el diccionario.
     * <p>
     * Cada instancia de {@code Balde} almacena una única asociación clave-valor.
     * La clave y el valor son finales, lo que significa que una vez que se crea
     * un {@code Balde}, su clave y valor no pueden cambiar.
     * </p>
     * <p>
     * La igualdad de dos instancias de {@code Balde} se basa únicamente en la
     * igualdad de sus claves. El valor no se considera en la comparación
     * {@code equals()}.
     * </p>
     *
     * @param <K> el tipo de la clave en esta entrada.
     * @param <V> el tipo del valor en esta entrada.
     */
    private static class Balde<K, V> {
        /**
         * La clave para esta entrada. No puede ser {@code null}.
         */
        final K llave;
        /**
         * El valor asociado con la clave en esta entrada. Puede ser {@code null}.
         */
        final V valor;

        /**
         * Construye una nueva entrada (balde) con la clave y el valor especificados.
         *
         * @param llave la clave para esta entrada.
         * @param valor el valor para esta entrada.
         */
        public Balde(K llave, V valor) {
            this.llave = llave;
            this.valor = valor;
        }

        /**
         * Compara este balde con el objeto especificado para determinar la igualdad.
         * <p>
         * Dos instancias de {@code Balde} se consideran iguales si y solo si el
         * objeto especificado también es una instancia de {@code Balde} y sus
         * claves son iguales según el método {@link Objects#equals(Object, Object)}.
         * El valor almacenado en el balde no se considera para la igualdad.
         * </p>
         * <p>
         * Esta implementación utiliza "pattern matching for instanceof" (Java 16+)
         * para un casteo más conciso y seguro.
         * </p>
         *
         * @param objeto el objeto con el que se va a comparar.
         * @return {@code true} si el objeto especificado es igual a este balde;
         * {@code false} en caso contrario.
         */
        @Override
        public boolean equals(Object objeto) {
            boolean esIgual = false; // Por defecto, no son iguales
            if (this == objeto) {
                esIgual = true;
            } else if (objeto instanceof Balde<?, ?> entrada) {
                // Si objeto es una instancia de Balde<?, ?>, se castea
                // automáticamente a 'Balde' como 'entrada' que queda disponible acá.
                // Es necesario aplicar el comodín <?, ?> para la comparación genérica
                // correcta, nó interesa saber de qué tipo es realmente, como su
                // erasure es suficiente.
                esIgual = Objects.equals(llave, entrada.llave);
            }
            // Si objeto es null o no es una instancia de Balde, esIgual permanece false.
            return esIgual;
        }

        /**
         * Devuelve el código hash para este balde.
         * <p>
         * El código hash se basa únicamente en el código hash de la clave.
         * El valor no se incluye en el cálculo del código hash, lo que es
         * consistente con la implementación de {@link #equals(Object)}.
         * </p>
         *
         * @return el código hash para este balde.
         */
        @Override
        public int hashCode() {
            return Objects.hash(llave);
        }

        @Override
        public String toString() {
            return "Balde{" +
                    "llave=" + llave +
                    ", valor=" + valor +
                    '}';
        }
    }
}