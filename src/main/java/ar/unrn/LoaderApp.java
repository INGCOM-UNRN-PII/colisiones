package ar.unrn;

import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Esta clase utilitaria se encarga de cargar dinámicamente y ejecutar todos los métodos `main`
 * encontrados en el paquete {@code ar.unrn} y sus subpaquetes.
 * Su propósito principal es automatizar la ejecución de los ejemplos de los trabajos prácticos.
 * No es necesaria (o recomendada) su modificación para el desarrollo de los TPs.
 *
 * @author martinvilu
 * @version 2026.04.23
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class LoaderApp {

    /**
     * Esta configuración no debiera de ser necesario cambiar.
     * La organización de los proyectos debe ser la misma para todo.
     * _you've got a package to run_
     */
    private static final String PACKAGE_TO_RUN = "ar.unrn";

    /**
     * Constructor privado para evitar la instanciación de esta clase utilitaria.
     */
    private LoaderApp() {
        // Constructor privado para clase utilitaria
    }

    /**
     * Punto de entrada principal de la aplicación.
     * Este método escanea el paquete {@code ar.unrn}, busca todas las clases que contienen
     * un método `main(String[] args)` y las ejecuta secuencialmente.
     *
     * @param args son los argumentos de invocación que serán pasados a los
     *             métodos `main` de las clases encontradas.
     * @throws InternalLoaderException si ocurre un error durante la carga o ejecución de las clases.
     * @contract.post Todos los métodos `main` de las clases encontradas en {@code PACKAGE_TO_RUN}
     *                (excepto {@code LoaderApp} misma) han sido invocados.
     */
    public static void main(String[] args) {
        Class[] clases;
        try {
            clases = getClasses(PACKAGE_TO_RUN);
        } catch (ClassNotFoundException e) {
            throw new InternalLoaderException("El paquete no existe", e);
        } catch (IOException e) {
            throw new InternalLoaderException("Error de acceso al recurso", e);
        }
        for (Class klass : clases) {
            String actual = klass.getName();
            if (!klass.equals(LoaderApp.class)) {
                System.out.printf("-Start: %s-----------%n", actual);
                try {
                    Method principal = klass.getMethod("main", String[].class);
                    try {
                        principal.invoke(null, (Object) args);
                    } catch (IllegalAccessException e) {
                        throw new InternalLoaderException("Fallo de permisos", e);
                    } catch (InvocationTargetException e) {
                        System.out.printf("Excepción al llamar el main de %s%n", actual);
                        e.printStackTrace();
                    }
                } catch (NoSuchMethodException e) {
                    System.out.printf("La clase '%s': no posee un main%n", actual);
                }
            }
            System.out.println("-End.-----");
        }
    }

    /**
     * Escanea todas las clases accesibles desde el ClassLoader del contexto
     * que pertenecen al paquete dado y sus subpaquetes.
     *
     * @param packageName El paquete base a escanear (ej. "ar.unrn").
     * @return Un arreglo de objetos {@code Class} encontrados en el paquete.
     * @throws ClassNotFoundException si un archivo parece una clase pero no puede ser cargado como tal.
     * @throws IOException            si hay problemas de acceso a los recursos del sistema de archivos.
     * @contract.pre {@code packageName != null && !packageName.isBlank()}
     * @contract.post Retorna un arreglo de clases no nulo.
     */
    private static Class[] getClasses(String packageName) throws
            ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null; // El ClassLoader del contexto no debería ser null en un entorno Java estándar.
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[0]);
    }

    /**
     * Método recursivo utilizado para encontrar todas las clases en un directorio dado
     * y sus subdirectorios.
     *
     * @param directory   El directorio base a escanear.
     * @param packageName El nombre del paquete para las clases encontradas dentro del directorio base.
     * @return Una lista de objetos {@code Class} encontrados en el directorio especificado.
     * @throws ClassNotFoundException si un archivo parece una clase pero no puede ser cargado como tal.
     * @contract.pre {@code directory != null && packageName != null && !packageName.isBlank()}
     * @contract.post Retorna una lista de clases no nula.
     */
    private static List<Class> findClasses(File directory, String packageName)
            throws ClassNotFoundException {
        List<Class> classes = new ArrayList<>();
        if (directory.exists()) {
            File[] files = directory.listFiles();
            assert files != null; // listFiles() puede retornar null si no es un directorio o hay error de I/O.
            for (File file : files) {
                String fileName = file.getName();
                if (file.isDirectory()) {
                    assert !fileName.contains("."); // Los nombres de directorio no deberían contener puntos.
                    classes.addAll(findClasses(file, packageName + "." + fileName));
                } else if (fileName.endsWith(".class")) {
                    final int extension = ".class".length();
                    String klassName = packageName + '.'
                            + fileName.substring(0, fileName.length() - extension);
                    classes.add(Class.forName(klassName));
                }
            }
        }
        return classes;
    }

    /**
     * Esta excepción indica fallos internos del cargador de mains.
     * Es una {@code RuntimeException} para no forzar su captura en los métodos `main` de los TPs.
     */
    public static class InternalLoaderException extends RuntimeException {
        /**
         * Forma parte de lo necesario para crear Excepciones y viene por
         * Serializable.
         */
        @Serial
        private static final long serialVersionUID = 42L;
        /**
         * Los fallos internos, provienen exclusivamente de otros por lo
         * que se encadenan. Es obligatorio agregar también contexto textual.
         *
         * @param message la descripción de la situación que provoco el problema.
         * @param reason  la excepción especifica que fue recibida.
         * @contract.pre {@code message != null && reason != null}
         * @contract.post La excepción es creada con el mensaje y la causa encadenada.
         */
        public InternalLoaderException(String message, Throwable reason) {
            super(message, reason);
        }
    }
}
