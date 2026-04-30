package ar.unrn;

import ar.unrn.diccionario.Diccionario;
import ar.unrn.diccionario.excepciones.ColisionException; // Asegúrate de importar tus excepciones
import ar.unrn.diccionario.excepciones.LlaveNulaException;  // Asegúrate de importar tus excepciones

/**
 * Aplicación de demostración para la clase Diccionario.
 * Muestra el uso básico, el manejo de claves nulas y las colisiones.
 */
public class DiccionarioApp {

    public static void main(String[] args) {
        Diccionario<String, Integer> diccionario = new Diccionario<>();

        System.out.println("--- Pruebas Básicas ---");
        System.out.println("¿Diccionario vacío al inicio? " + diccionario.isEmpty()); // true
        System.out.println("Tamaño inicial: " + diccionario.size()); // 0

        // Inserciones exitosas
        try {
            System.out.println("\nIntentando insertar 'manzana':");
            if (diccionario.put("manzana", 10)) {
                System.out.println("  'manzana' insertada correctamente.");
            }
            System.out.println("Intentando insertar 'banana':");
            if (diccionario.put("banana", 20)) {
                System.out.println("  'banana' insertada correctamente.");
            }
            System.out.println("Intentando insertar 'cereza':");
            if (diccionario.put("cereza", 30)) {
                System.out.println("  'cereza' insertada correctamente.");
            }
        } catch (ColisionException | LlaveNulaException e) {
            System.err.println("Error inesperado durante inserciones básicas: " + e.getMessage());
        }

        System.out.println("\n--- Recuperación de Valores ---");
        System.out.println("Valor de 'manzana': " + diccionario.get("manzana")); // 10
        System.out.println("Valor de 'banana': " + diccionario.get("banana")); // 20
        System.out.println("Valor de 'cereza': " + diccionario.get("cereza")); // 30
        System.out.println("Valor de 'uva' (no existe): " + diccionario.get("uva")); // null
        System.out.println("Tamaño actual: " + diccionario.size()); // 3
        System.out.println("¿Diccionario vacío ahora? " + diccionario.isEmpty()); // false

        System.out.println("\n--- Prueba de Llave Nula ---");
        try {
            System.out.println("Intentando insertar clave nula...");
            diccionario.put(null, 100);
        } catch (LlaveNulaException e) {
            System.out.println("  Excepción capturada (esperado): " + e.getMessage());
        }

        try {
            System.out.println("Intentando obtener valor con clave nula...");
            diccionario.get(null);
        } catch (LlaveNulaException e) {
            System.out.println("  Excepción capturada (esperado): " + e.getMessage());
        }

        try {
            System.out.println("Intentando remover con clave nula...");
            diccionario.remove(null);
        } catch (LlaveNulaException e) {
            System.out.println("  Excepción capturada (esperado): " + e.getMessage());
        }

        try {
            System.out.println("Intentando containsKey con clave nula...");
            diccionario.containsKey(null);
        } catch (LlaveNulaException e) {
            System.out.println("  Excepción capturada (esperado): " + e.getMessage());
        }


        System.out.println("\n--- Prueba de Colisiones ---");
        // Estas claves están diseñadas para colisionar si NUM_BUCKETS = 256
        // "clave_colision_1".hashCode() = -2110849689 -> Math.floorMod(..., 256) = 103
        // "otra_clave_muy_diferente_para_colision".hashCode() = 1300600423 -> Math.floorMod(..., 256) = 103
        String claveColision1 = "clave_colision_1";
        String claveColision2 = "otra_clave_muy_diferente_para_colision";

        try {
            System.out.println("Intentando insertar '" + claveColision1 + "':");
            if (diccionario.put(claveColision1, 100)) {
                System.out.println("  '" + claveColision1 + "' insertada correctamente.");
            }
        } catch (ColisionException e) {
            System.err.println("  Error inesperado al insertar la primera clave de colisión: " + e.getMessage());
        }
        System.out.println("Tamaño después de insertar '" + claveColision1 + "': " + diccionario.size());

        try {
            System.out.println("Intentando insertar '" + claveColision2 + "' (debería colisionar con '" + claveColision1 + "'):");
            diccionario.put(claveColision2, 200); // Esto debería lanzar ColisionException
            System.out.println("  '" + claveColision2 + "' se insertó (inesperado).");
        } catch (ColisionException e) {
            System.out.println("  Excepción de colisión capturada (esperado): " + e.getMessage());
        }

        System.out.println("\n--- Verificación después de intento de colisión ---");
        System.out.println("Valor de '" + claveColision1 + "': " + diccionario.get(claveColision1)); // Debería ser 100
        System.out.println("Valor de '" + claveColision2 + "' (no debería existir): " + diccionario.get(claveColision2)); // Debería ser null
        System.out.println("¿Contiene '" + claveColision1 + "'? " + diccionario.containsKey(claveColision1)); // true
        System.out.println("¿Contiene '" + claveColision2 + "'? " + diccionario.containsKey(claveColision2)); // false
        System.out.println("Tamaño final (después de intento de colisión): " + diccionario.size()); // Debería ser 4 (manzana, banana, cereza, claveColision1)

        System.out.println("\n--- Prueba de re-inserción de la misma clave (debería colisionar) ---");
        try {
            System.out.println("Intentando re-insertar 'manzana' con un nuevo valor:");
            diccionario.put("manzana", 101); // 'manzana' ya existe, el bucket está ocupado por ella misma
            System.out.println("  'manzana' se re-insertó (inesperado bajo la política actual).");
        } catch (ColisionException e) {
            System.out.println("  Excepción de colisión capturada (esperado al re-insertar): " + e.getMessage());
        }
        System.out.println("Valor de 'manzana' después del intento de re-inserción: " + diccionario.get("manzana")); // Debería seguir siendo 10


        System.out.println("\n--- Pruebas de Eliminación ---");
        System.out.println("Intentando eliminar 'banana': " + diccionario.remove("banana")); // true
        System.out.println("Valor de 'banana' después de eliminar: " + diccionario.get("banana")); // null
        System.out.println("Tamaño después de eliminar 'banana': " + diccionario.size());

        System.out.println("Intentando eliminar 'inexistente': " + diccionario.remove("inexistente")); // false
        System.out.println("Tamaño después de intentar eliminar 'inexistente': " + diccionario.size());

        System.out.println("\n--- Verificación final ---");
        System.out.println("¿Contiene 'manzana'? " + diccionario.containsKey("manzana")); // true
        System.out.println("¿Contiene 'banana'? " + diccionario.containsKey("banana")); // false
        System.out.println("¿Diccionario vacío al final? " + diccionario.isEmpty());
        System.out.println("Tamaño final del diccionario: " + diccionario.size());

        System.out.println(diccionario);
        System.out.println("\nDemostración finalizada.");
    }
}