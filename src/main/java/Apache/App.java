package Apache;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import Apache.server.MicroSpringBoot;
import Apache.server.SimpleWebServer;
import Apache.util.ImageGenerator;

/**
 * Punto de entrada del servidor web MicroSpringBoot.
 *
 * Uso: java -cp target/classes Apache.App [clase_controlador]
 *
 * Si se pasa una clase como argumento, se carga ese controlador especifico. Si
 * no se pasa argumento, se escanea el paquete Apache.controllers
 * automaticamente.
 */
public class App {

    private static final int DEFAULT_PORT = 8080;
    private static final String STATIC_FILES_DIR = "src/main/resources/static";

    public static void main(String[] args) {
        try {
            // Generar imagen de prueba si no existe
            Path imgPath = Paths.get(STATIC_FILES_DIR, "img", "test.png");
            if (!Files.exists(imgPath)) {
                ImageGenerator.generateTestImage(imgPath.toString());
                System.out.println("Imagen de prueba generada en: " + imgPath);
            }

            // Crear framework IoC
            MicroSpringBoot framework = new MicroSpringBoot();

            if (args.length > 0) {
                // Modo 1: Cargar controlador desde linea de comandos
                for (String className : args) {
                    System.out.println("Cargando controlador: " + className);
                    framework.registerController(className);
                }
            } else {
                // Modo 2: Escanear classpath buscando @RestController
                System.out.println("Escaneando componentes en Apache.controllers...");
                framework.scanComponents("Apache.controllers");
            }

            // Iniciar servidor
            SimpleWebServer server = new SimpleWebServer(DEFAULT_PORT, STATIC_FILES_DIR, framework);
            server.start();

        } catch (Exception e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
