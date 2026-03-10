package co.edu.escuelaing.reflexionlab;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import co.edu.escuelaing.reflexionlab.annotations.GetMapping;

public class ControllerLoader {

    public static Map<String, Method> services = new HashMap<>();
    public static Map<String, Object> controllerInstances = new HashMap<>();

    public static void load(Class<?> controllerClass) throws Exception {
        Object instance = controllerClass.getDeclaredConstructor().newInstance();
        for (Method method : controllerClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(GetMapping.class)) {
                GetMapping mapping = method.getAnnotation(GetMapping.class);
                services.put(mapping.value(), method);
                controllerInstances.put(mapping.value(), instance);
            }
        }
    }
}
