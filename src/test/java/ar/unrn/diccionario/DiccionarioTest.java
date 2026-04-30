package ar.unrn.diccionario;

import ar.unrn.diccionario.excepciones.ColisionException;
import ar.unrn.diccionario.excepciones.LlaveNulaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas para la clase Diccionario")
class DiccionarioTest {

    // Claves que sabemos que colisionarán con NUM_BUCKETS = 256
    // ... (resto de tus comentarios y campos de clase sin cambios) ...
    private final String CLAVE_COLISION_1 = "clave_colision_1"; // Su índice de bucket es 103
    private final String CLAVE_COLISION_2 = "otra_clave_muy_diferente_para_colision"; // Su índice de bucket también es 103
    private Diccionario<String, Integer> diccionario;
    private Diccionario<Integer, String> diccionarioNumerico;


    @BeforeEach
    void setUp() {
        diccionario = new Diccionario<>();
        diccionarioNumerico = new Diccionario<>();
    }

    @Test
    @DisplayName("Un diccionario nuevo debe estar vacío y tener tamaño 0")
    void nuevoDiccionarioEstaVacio() {
        assertTrue(diccionario.isEmpty(), "El diccionario nuevo debería estar vacío.");
        assertEquals(0, diccionario.size(), "El tamaño del diccionario nuevo debería ser 0.");
    }

    @Test
    @DisplayName("Prueba de múltiples operaciones con diferentes tipos de clave/valor")
    void multiplesOperacionesConTiposDiferentes() {
        diccionarioNumerico.put(1, "uno");
        diccionarioNumerico.put(2, "dos");
        diccionarioNumerico.put(300, "trescientos"); // 300 % 256 = 44

        assertEquals("uno", diccionarioNumerico.get(1));
        assertEquals("trescientos", diccionarioNumerico.get(300));
        assertEquals(3, diccionarioNumerico.size());

        assertTrue(diccionarioNumerico.remove(2));
        assertEquals(2, diccionarioNumerico.size());
        assertNull(diccionarioNumerico.get(2));

        // Prueba de colisión con claves numéricas
        final int CLAVE_NUM_COLISION_EXISTENTE = 300; // Ya insertada, bucket 44
        final int CLAVE_NUM_COLISION_NUEVA = 556;   // Mapea al mismo bucket 44

        assertThrows(ColisionException.class, () -> {
            diccionarioNumerico.put(CLAVE_NUM_COLISION_NUEVA, "quinientos cincuenta y seis");
        });
        assertEquals("trescientos", diccionarioNumerico.get(CLAVE_NUM_COLISION_EXISTENTE));
        assertNull(diccionarioNumerico.get(CLAVE_NUM_COLISION_NUEVA));
        assertEquals(2, diccionarioNumerico.size());
    }

    @Nested
    @DisplayName("Pruebas para el método put()")
    class PutTests {

        @Test
        @DisplayName("Debe insertar un par clave-valor correctamente en un bucket vacío")
        void put_InsertaCorrectamente() {
            assertTrue(diccionario.put("clave1", 100), "put debería retornar true para una inserción exitosa.");
            assertEquals(1, diccionario.size(), "El tamaño debería ser 1 después de una inserción.");
            assertFalse(diccionario.isEmpty(), "El diccionario no debería estar vacío después de una inserción.");
            assertEquals(100, diccionario.get("clave1"), "get debería retornar el valor insertado.");
        }

        @Test
        @DisplayName("Debe lanzar LlaveNulaException si la clave es nula")
        void put_LanzaLlaveNulaException() {
            Exception exception = assertThrows(LlaveNulaException.class, () -> {
                diccionario.put(null, 100);
            });
            assertEquals("La clave no puede ser nula.", exception.getMessage());
        }

        @Test
        @DisplayName("Debe lanzar ColisionException si el bucket ya está ocupado por otra clave")
        void put_LanzaColisionException() {
            diccionario.put(CLAVE_COLISION_1, 10); // Ocupa el bucket

            System.out.println(diccionario.obtienePosicion(CLAVE_COLISION_1));
            System.out.println(diccionario.obtienePosicion(CLAVE_COLISION_2));

            ColisionException exception = assertThrows(ColisionException.class, () -> {
                diccionario.put(CLAVE_COLISION_1, 20); // Intenta insertar otra clave en el mismo bucket
            });

            // Verificación del mensaje de la excepción (más robusta usando contains)
            String exceptionMessage = exception.getMessage();
            assertTrue(exceptionMessage.contains("Colisión en el bucket"), "El mensaje debe indicar colisión en bucket.");
            assertTrue(exceptionMessage.contains("al intentar insertar la clave: " + CLAVE_COLISION_1), "El mensaje debe indicar la clave que falló.");
            assertTrue(exceptionMessage.contains("El bucket ya está ocupado por la clave: " + CLAVE_COLISION_1), "El mensaje debe indicar la clave existente.");

            assertEquals(1, diccionario.size(), "El tamaño no debería cambiar después de una colisión.");
            assertEquals(10, diccionario.get(CLAVE_COLISION_1), "El valor original debería permanecer.");
            assertNull(diccionario.get(CLAVE_COLISION_2), "La clave que causó la colisión no debería estar presente.");
        }

        @Test
        @DisplayName("put con la misma clave en un bucket ocupado por ella misma debe lanzar ColisionException (según política actual)")
        void put_MismaClaveEnBucketOcupadoLanzaColision() {
            diccionario.put("claveUnica", 1);
            ColisionException exception = assertThrows(ColisionException.class, () -> {
                diccionario.put("claveUnica", 2); // Intenta re-insertar la misma clave
            });
            assertTrue(exception.getMessage().contains("claveUnica"));
            assertEquals(1, diccionario.get("claveUnica"), "El valor original no debería cambiar.");
            assertEquals(1, diccionario.size());
        }
    }

    @Nested
    @DisplayName("Pruebas para el método get()")
    class GetTests {

        @Test
        @DisplayName("Debe retornar el valor correcto para una clave existente")
        void get_RetornaValorExistente() {
            diccionario.put("clave1", 10);
            diccionario.put("clave2", 20); // Asumiendo que "clave2" no colisiona con "clave1" de forma que impida la inserción
            assertEquals(10, diccionario.get("clave1"));
            assertEquals(20, diccionario.get("clave2"));
        }

        @Test
        @DisplayName("Debe retornar null para una clave no existente")
        void get_RetornaNullParaClaveNoExistente() {
            assertNull(diccionario.get("claveNoExistente"));
            diccionario.put("clave1", 10);
            assertNull(diccionario.get("claveNoExistente"));
        }

        @Test
        @DisplayName("Debe lanzar LlaveNulaException si la clave es nula")
        void get_LanzaLlaveNulaException() {
            Exception exception = assertThrows(LlaveNulaException.class, () -> {
                diccionario.get(null);
            });
            assertEquals("La clave no puede ser nula.", exception.getMessage());
        }

        @Test
        @DisplayName("Debe retornar null si el bucket está ocupado por una clave diferente (colisión no resuelta)")
        void get_RetornaNullSiBucketOcupadoPorOtraClave() {
            diccionario.put(CLAVE_COLISION_1, 100); // Ocupa el bucket
            // CLAVE_COLISION_2 mapea al mismo bucket pero no está insertada
            assertNull(diccionario.get(CLAVE_COLISION_2));
        }
    }

    @Nested
    @DisplayName("Pruebas para el método remove()")
    class RemoveTests {

        @Test
        @DisplayName("Debe remover un par clave-valor existente y retornar true")
        void remove_RemueveExistenteYRetornaTrue() {
            diccionario.put("claveAEliminar", 50);
            assertEquals(1, diccionario.size());
            assertTrue(diccionario.remove("claveAEliminar"), "remove debería retornar true para una clave existente.");
            assertEquals(0, diccionario.size(), "El tamaño debería ser 0 después de remover.");
            assertNull(diccionario.get("claveAEliminar"), "El valor debería ser null después de remover.");
            assertFalse(diccionario.containsKey("claveAEliminar"), "containsKey debería ser false después de remover.");
        }

        @Test
        @DisplayName("Debe retornar false para una clave no existente")
        void remove_RetornaFalseParaClaveNoExistente() {
            assertFalse(diccionario.remove("claveNoExistente"));
            diccionario.put("clave1", 10);
            assertFalse(diccionario.remove("claveNoExistente"));
            assertEquals(1, diccionario.size());
        }

        @Test
        @DisplayName("Debe lanzar LlaveNulaException si la clave es nula")
        void remove_LanzaLlaveNulaException() {
            Exception exception = assertThrows(LlaveNulaException.class, () -> {
                diccionario.remove(null);
            });
            assertEquals("La clave no puede ser nula.", exception.getMessage());
        }

        @Test
        @DisplayName("Debe retornar false si el bucket está ocupado por una clave diferente")
        void remove_RetornaFalseSiBucketOcupadoPorOtraClave() {
            diccionario.put(CLAVE_COLISION_1, 100); // Ocupa el bucket
            // CLAVE_COLISION_2 mapea al mismo bucket pero no está insertada
            assertFalse(diccionario.remove(CLAVE_COLISION_2));
            assertEquals(1, diccionario.size()); // El tamaño no debe cambiar
            assertTrue(diccionario.containsKey(CLAVE_COLISION_1)); // La clave original debe seguir allí
        }
    }

    @Nested
    @DisplayName("Pruebas para el método containsKey()")
    class ContainsKeyTests {

        @Test
        @DisplayName("Debe retornar true para una clave existente")
        void containsKey_RetornaTrueParaClaveExistente() {
            diccionario.put("claveExistente", 1);
            assertTrue(diccionario.containsKey("claveExistente"));
        }

        @Test
        @DisplayName("Debe retornar false para una clave no existente")
        void containsKey_RetornaFalseParaClaveNoExistente() {
            assertFalse(diccionario.containsKey("claveNoExistente"));
            diccionario.put("clave1", 10);
            assertFalse(diccionario.containsKey("claveNoExistente"));
        }

        @Test
        @DisplayName("Debe lanzar LlaveNulaException si la clave es nula")
        void containsKey_LanzaLlaveNulaException() {
            Exception exception = assertThrows(LlaveNulaException.class, () -> {
                diccionario.containsKey(null);
            });
            assertEquals("La clave no puede ser nula.", exception.getMessage());
        }

        @Test
        @DisplayName("Debe retornar false si el bucket está ocupado por una clave diferente")
        void containsKey_RetornaFalseSiBucketOcupadoPorOtraClave() {
            diccionario.put(CLAVE_COLISION_1, 100); // Ocupa el bucket
            // CLAVE_COLISION_2 mapea al mismo bucket pero no está insertada
            assertFalse(diccionario.containsKey(CLAVE_COLISION_2));
        }
    }

    @Nested
    @DisplayName("Pruebas para los métodos size() e isEmpty()")
    class SizeAndIsEmptyTests {

        @Test
        @DisplayName("size() debe retornar el número correcto de elementos y manejar colisiones")
        void size_RetornaNumeroCorrectoYManejaColisiones() {
            // 1. Estado inicial
            assertEquals(0, diccionario.size(), "Tamaño inicial debe ser 0.");

            // 2. Inserciones exitosas
            diccionario.put("a", 1);
            assertEquals(1, diccionario.size(), "Tamaño después de 1 inserción.");
            diccionario.put("b", 2); // Asumir que "b" no colisiona con "a"
            assertEquals(2, diccionario.size(), "Tamaño después de 2 inserciones.");

            // 3. Inserción de una clave que colisionará después
            diccionario.put(CLAVE_COLISION_1, 3); // Asumir que no colisiona con "a" o "b"
            assertEquals(3, diccionario.size(), "Tamaño después de insertar la primera clave de colisión.");

            // 4. Intento de inserción de una clave que causa colisión
            assertThrows(ColisionException.class, () -> {
                diccionario.put(CLAVE_COLISION_1, 4); // Esto debería fallar
            }, "Debería lanzarse ColisionException al insertar una clave en un bucket ocupado.");
            assertEquals(3, diccionario.size(), "El tamaño no debe cambiar después de una ColisionException.");

            // 5. Pruebas de eliminación
            diccionario.remove("a");
            assertEquals(2, diccionario.size(), "Tamaño después de eliminar 'a'.");
            diccionario.remove(CLAVE_COLISION_1);
            assertEquals(1, diccionario.size(), "Tamaño después de eliminar CLAVE_COLISION_1.");
            diccionario.remove("b");
            assertEquals(0, diccionario.size(), "Tamaño después de eliminar 'b'.");
        }

        @Test
        @DisplayName("isEmpty() debe funcionar correctamente")
        void isEmpty_FuncionaCorrectamente() {
            assertTrue(diccionario.isEmpty(), "Diccionario debe estar vacío inicialmente.");
            diccionario.put("a", 1);
            assertFalse(diccionario.isEmpty(), "Diccionario no debe estar vacío después de una inserción.");
            diccionario.remove("a");
            assertTrue(diccionario.isEmpty(), "Diccionario debe estar vacío después de remover el único elemento.");
        }
    }
    // ... (resto de la clase, como el helper comentado, sin cambios) ...
}