package ar.unrn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColisionApp {
    private static final int NUM_OBJETOS_A_GENERAR = 500_000;

    public static void main(String[] args) {
        System.out.println("Iniciando búsqueda de colisiones de hashCode para " + NUM_OBJETOS_A_GENERAR + " objetos...");

        Map<Integer, List<ObjetoSimple>> objetosPorHashCode = new HashMap<>();

        for (int i = 0; i < NUM_OBJETOS_A_GENERAR; i++) {
            ObjetoSimple obj = ObjetoSimple.crearSiguiente();
            int hashCode = obj.hashCode();

            List<ObjetoSimple> listaParaEsteHashCode = objetosPorHashCode.get(hashCode);
            if (listaParaEsteHashCode == null) {
                listaParaEsteHashCode = new ArrayList<>();
                objetosPorHashCode.put(hashCode, listaParaEsteHashCode);
            }
            listaParaEsteHashCode.add(obj);
        }

        System.out.println("\nGeneración y agrupación completada.");
        System.out.println("Número total de objetos generados: " + NUM_OBJETOS_A_GENERAR);
        System.out.println("Número de hashCodes únicos encontrados: " + objetosPorHashCode.size());

        int colisionesEncontradas = 0;
        System.out.println("\n--- Colisiones Encontradas (hashCodes con más de un objeto) ---");

        for (Map.Entry<Integer, List<ObjetoSimple>> entry : objetosPorHashCode.entrySet()) {
            List<ObjetoSimple> listaObjetos = entry.getValue();

            if (listaObjetos.size() > 1) {
                colisionesEncontradas++;
                Integer hashCode = entry.getKey();

                System.out.println("HashCode: " + hashCode + " (Número de objetos: " + listaObjetos.size() + ")");
                for (ObjetoSimple obj : listaObjetos) {
                    System.out.println("  - " + obj);
                }
                System.out.println("---");
            }
        }

        System.out.println("\nResumen:");
        System.out.println("Total de objetos generados: " + NUM_OBJETOS_A_GENERAR);
        System.out.println("Total de colisiones de hashCode encontradas: " + colisionesEncontradas);
    }
}