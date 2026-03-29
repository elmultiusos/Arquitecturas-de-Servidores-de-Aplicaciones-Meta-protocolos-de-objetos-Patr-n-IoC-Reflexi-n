package Apache.server;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Apache.annotations.GetMapping;
import Apache.annotations.RequestParam;
import Apache.annotations.RestController;

/**
 * Framework IoC minimo que usa reflexion de Java para descubrir clases anotadas
 * con @RestController y registrar metodos anotados con @GetMapping. Soporta
 * @RequestParam para inyeccion de parametros de consulta.
 */
public class MicroSpringBoot {

    // Mapa de URI -> handler (objeto + metodo)
    private final Map<String, HandlerEntry> routeMap = new HashMap<>();

    /**
     * Entrada que almacena la instancia del controlador y el metodo a invocar.
     */
    private static class HandlerEntry {

        final Object instance;
        final Method method;

        HandlerEntry(Object instance, Method method) {
            this.instance = instance;
            this.method = method;
        }
    }

    /**
     * Registra un controlador dado su nombre de clase completo (FQCN). Usado
     * para cargar desde la linea de comandos.
     */
    public void registerController(String className) throws Exception {
        Class<?> clazz = Class.forName(className);
        registerControllerClass(clazz);
    }

    /**
     * Registra una clase de controlador si tiene @RestController.
     */
    public void registerControllerClass(Class<?> clazz) throws Exception {
        if (!clazz.isAnnotationPresent(RestController.class)) {
            System.out.println("La clase " + clazz.getName() + " no tiene @RestController, ignorada.");
            return;
        }

        Object instance = clazz.getDeclaredConstructor().newInstance();
        System.out.println("Registrando controlador: " + clazz.getName());

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(GetMapping.class)) {
                GetMapping mapping = method.getAnnotation(GetMapping.class);
                String uri = mapping.value();
                routeMap.put(uri, new HandlerEntry(instance, method));
                System.out.println("  GET " + uri + " -> " + method.getName() + "()");
            }
        }
    }

    /**
     * Escanea el classpath buscando todas las clases con @RestController dentro
     * del paquete base dado.
     */
    public void scanComponents(String basePackage) {
        try {
            String path = basePackage.replace('.', '/');
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources(path);

            List<Class<?>> classes = new ArrayList<>();

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.toURI());
                if (directory.exists()) {
                    findClasses(directory, basePackage, classes);
                }
            }

            for (Class<?> clazz : classes) {
                if (clazz.isAnnotationPresent(RestController.class)) {
                    registerControllerClass(clazz);
                }
            }

            System.out.println("Escaneo completado. Rutas registradas: " + routeMap.size());

        } catch (Exception e) {
            System.err.println("Error al escanear componentes: " + e.getMessage());
        }
    }

    /**
     * Busca recursivamente archivos .class en un directorio.
     */
    private void findClasses(File directory, String packageName, List<Class<?>> classes) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                findClasses(file, packageName + "." + file.getName(), classes);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                try {
                    classes.add(Class.forName(className));
                } catch (ClassNotFoundException e) {
                    System.err.println("No se pudo cargar la clase: " + className);
                }
            }
        }
    }

    /**
     * Maneja una solicitud HTTP. Retorna la respuesta como String, o null si no
     * hay ruta registrada para el path dado.
     */
    public String handleRequest(String path, Map<String, String> queryParams) {
        HandlerEntry entry = routeMap.get(path);
        if (entry == null) {
            return null;
        }

        try {
            Method method = entry.method;
            Parameter[] parameters = method.getParameters();

            if (parameters.length == 0) {
                // Metodo sin parametros
                return (String) method.invoke(entry.instance);
            }

            // Construir argumentos basados en @RequestParam
            Object[] args = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Parameter param = parameters[i];
                if (param.isAnnotationPresent(RequestParam.class)) {
                    RequestParam rp = param.getAnnotation(RequestParam.class);
                    String paramName = rp.value();
                    String defaultValue = rp.defaultValue();
                    String value = queryParams.getOrDefault(paramName, defaultValue);
                    args[i] = value;
                } else {
                    args[i] = null;
                }
            }

            return (String) method.invoke(entry.instance, args);

        } catch (Exception e) {
            System.err.println("Error al invocar handler para " + path + ": " + e.getMessage());
            e.printStackTrace();
            return "<html><body><h1>500 Internal Server Error</h1><p>" + e.getMessage() + "</p></body></html>";
        }
    }

    /**
     * Retorna las rutas registradas (para debug).
     */
    public Set<String> getRegisteredRoutes() {
        return Collections.unmodifiableSet(routeMap.keySet());
    }
}
