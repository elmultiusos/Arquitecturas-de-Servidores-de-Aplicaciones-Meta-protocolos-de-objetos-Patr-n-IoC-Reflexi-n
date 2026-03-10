package co.edu.escuelaing.reflexionlab;

import co.edu.escuelaing.reflexionlab.annotations.RestController;

public class MicroSpringBoot {

    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            System.out.println("Debe enviar el controlador como argumento");
            return;
        }

        Class<?> controllerClass = Class.forName(args[0]);

        if (controllerClass.isAnnotationPresent(RestController.class)) {
            ControllerLoader.load(controllerClass);
        }

        HttpServer.start();
    }
}
