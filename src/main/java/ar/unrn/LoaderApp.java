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
 * Esta clase se encarga de ejecutar todos los main en esta ubicación.
 * No es necesaria (o recomendada) su modificación.
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
     * Punto de entrada del trabajo práctico, este método se encarga de ejecutar
     * todos los contenidos en esta ubicación.
     *
     * @param args son los argumentos de invocación que serán pasados a los
     *             otros main.
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

    // Métodos extraídos de:
    // http://snippets.dzone.com/posts/show/4831

    /**
     * Scans all classes accessible from the context class loader which belong
     * to the given package and subpackages.
     *
     * @param packageName The base package to fetch
     * @return The classes in packageName
     * @throws ClassNotFoundException when a file looks like a class but isn't
     * @throws IOException            when a file has access problems
     */
    private static Class[] getClasses(String packageName) throws
            ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
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
     * Recursive method used to find all classes in a given directory
     * and subdirectories.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base
     *                    directory
     * @return The classes in the specified directory
     * @throws ClassNotFoundException when a file looks like a class but isn't
     */
    private static List<Class> findClasses(File directory, String packageName)
            throws ClassNotFoundException {
        List<Class> classes = new ArrayList<>();
        if (directory.exists()) {
            File[] files = directory.listFiles();
            assert files != null;
            for (File file : files) {
                String fileName = file.getName();
                if (file.isDirectory()) {
                    assert !fileName.contains(".");
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
         */
        public InternalLoaderException(String message, Throwable reason) {
            super(message, reason);
        }
    }
}
