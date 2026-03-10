package co.edu.escuelaing.reflexionlab;

import co.edu.escuelaing.reflexionlab.annotations.RestController;

public class MicroSpringBoot {

    public static void main(String[] args) throws Exception {
        // Explorar paquetes y cargar todos los controladores con @RestController
        String[] controllerPackages = {
            "co.edu.escuelaing.controllers",
            "co.edu.escuelaing.reflexionlab"
        };
        for (String pkg : controllerPackages) {
            for (Class<?> clazz : ClassFinder.find(pkg)) {
                if (clazz.isAnnotationPresent(RestController.class)) {
                    ControllerLoader.load(clazz);
                }
            }
        }
        HttpServer.start();
    }
}
