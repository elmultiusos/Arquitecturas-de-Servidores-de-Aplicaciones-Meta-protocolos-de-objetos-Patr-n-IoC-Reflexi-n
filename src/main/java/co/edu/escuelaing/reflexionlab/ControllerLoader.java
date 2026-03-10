package co.edu.escuelaing.reflexionlab;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import co.edu.escuelaing.reflexionlab.annotations.GetMapping;

public class ControllerLoader {

    public static Map<String, Method> services = new HashMap<>();
    public static Object controllerInstance;

    public static void load(Class<?> controllerClass) throws Exception {

        controllerInstance = controllerClass.getDeclaredConstructor().newInstance();

        for (Method method : controllerClass.getDeclaredMethods()) {

            if (method.isAnnotationPresent(GetMapping.class)) {

                GetMapping mapping = method.getAnnotation(GetMapping.class);

                services.put(mapping.value(), method);

            }
        }
    }
}
