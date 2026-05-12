package ar.unrn;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase principal que demuestra la búsqueda de colisiones de {@code hashCode}
 * para una gran cantidad de objetos {@code ObjetoSimple}.
 * También incluye un ejemplo básico de uso de la librería Lanterna para interacción con la terminal.
 *
 * @author martinvilu
 * @version 2026.04.23
 */
public class ColisionApp {
    /**
     * Número de objetos {@code ObjetoSimple} a generar para la búsqueda de colisiones.
     */
    private static final int NUM_OBJETOS_A_GENERAR = 500_000;

    /**
     * Constructor privado para evitar la instanciación de esta clase de utilidad.
     */
    private ColisionApp() {
        // Constructor privado para clase utilitaria
    }

    /**
     * Punto de entrada principal de la aplicación.
     * Realiza dos tareas principales:
     * 1. Demuestra un uso básico de la librería Lanterna para dibujar en la terminal.
     * 2. Genera una gran cantidad de {@code ObjetoSimple}, calcula sus {@code hashCode}
     *    y detecta cuántas colisiones ocurren (múltiples objetos con el mismo {@code hashCode}).
     *
     * @param args Argumentos de invocación (no utilizados en este ejemplo).
     * @throws IOException si ocurre un error de entrada/salida al interactuar con la terminal.
     * @throws InterruptedException si el hilo actual es interrumpido mientras espera.
     * @contract.post Se imprime en consola un resumen de las colisiones de hashCode encontradas.
     */
    public static void main(String[] args) {
        try {
            Screen screen = new DefaultTerminalFactory().createScreen();
            Terminal terminal = new DefaultTerminalFactory().createTerminal();
            screen.startScreen();
            TextGraphics textGraphics = screen.newTextGraphics();
            textGraphics.setForegroundColor(TextColor.ANSI.RED);
            textGraphics.setBackgroundColor(TextColor.ANSI.GREEN);
            textGraphics.putString(10, 5, "¡¡Hola mundo!!");
            screen.refresh();
            TerminalSize screenSize = terminal.getTerminalSize();

            //Place the cursor in the bottom right corner
            terminal.setCursorPosition(screenSize.getColumns() - 1, screenSize.getRows() - 1);
            terminal.setCursorPosition(10, 5);
            terminal.putString("Programación 2");
            terminal.newTextGraphics().drawLine(0, 10, 3, 5, 'x');
            terminal.putCharacter('H');
            terminal.putCharacter('e');
            terminal.putCharacter('l');
            terminal.putCharacter('l');
            terminal.putCharacter('o');
            terminal.putCharacter('!');
            terminal.setCursorPosition(0, 0);

            terminal.flush();
            Thread.sleep(2000);
            terminal.enableSGR(SGR.BOLD);
            terminal.putCharacter('Y');
            terminal.putCharacter('e');
            terminal.putCharacter('l');
            terminal.putCharacter('l');
            terminal.putCharacter('o');
            terminal.putCharacter('w');
            terminal.putCharacter(' ');
            terminal.putCharacter('o');
            terminal.putCharacter('n');
            terminal.putCharacter(' ');
            terminal.putCharacter('b');
            terminal.putCharacter('l');
            terminal.putCharacter('u');
            terminal.putCharacter('e');
            terminal.flush();
            Thread.sleep(2000);


            screen.startScreen();

            terminal.flush();
            screen.refresh();
        } catch (IOException | InterruptedException exception) {
            System.out.println("BOOM");
            System.exit(1);
        }
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

    /**
     * Clase interna estática que extiende {@code BasicWindow} de Lanterna.
     * Demuestra la creación de una ventana básica con un diseño de paneles horizontales.
     */
    public static class MyWindow extends BasicWindow {
        /**
         * Constructor para {@code MyWindow}.
         * Configura el título de la ventana y su contenido con un panel horizontal
         * que contiene tres paneles internos.
         * @contract.post La ventana es inicializada con un título y un diseño de paneles.
         */
        public MyWindow() {
            super("My Window!");
            Panel horizontalPanel = new Panel();
            horizontalPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
            Panel leftPanel = new Panel();
            Panel middlePanel = new Panel();
            Panel rightPanel = new Panel();

            horizontalPanel.addComponent(leftPanel);
            horizontalPanel.addComponent(middlePanel.withBorder(Borders.singleLineBevel("Panel Title")));
            horizontalPanel.addComponent(rightPanel.withBorder(Borders.doubleLineBevel()));

            // This ultimately links in the panels as the window content
            setComponent(horizontalPanel);
        }
    }
}
