# MicroSpringBoot

## Descripción

MicroSpringBoot es un framework minimalista en Java para construir aplicaciones web usando reflexión y el patrón IoC. Permite crear controladores POJO anotados, publicar servicios REST y extraer parámetros de consulta. Incluye un servidor HTTP capaz de entregar páginas HTML e imágenes PNG.

## Instalación

1. Clona el repositorio:
   ```
   git clone https://github.com/elmultiusos/Arquitecturas-de-Servidores-de-Aplicaciones-Meta-protocolos-de-objetos-Patr-n-IoC-Reflexi-n
   ```
2. Compila el proyecto con Maven:
   ```
   mvn clean package
   ```

## Ejecución

Para iniciar el servidor y cargar los controladores automáticamente:

```
java -cp target/classes co.edu.escuelaing.reflexionlab.MicroSpringBoot
```

## Uso

Ejemplo de controlador:

```java
@RestController
public class GreetingController {
    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hola " + name;
    }
}
```

Accede a:  
`http://localhost:35000/greeting?name=Juan`

## Estructura del Proyecto

- `src/main/java/co/edu/escuelaing/annotations`: Anotaciones personalizadas (`@RestController`, `@GetMapping`, `@RequestParam`)
- `src/main/java/co/edu/escuelaing/controllers`: Controladores de ejemplo
- `src/main/java/co/edu/escuelaing/reflexionlab`: Core del framework y servidor

## Pruebas

El proyecto incluye pruebas automatizadas en `src/test/java`.

Ejemplo de prueba unitaria:

```java
public void testControllerLoader() throws Exception {
     Class<?> controllerClass = Class.forName("co.edu.escuelaing.reflexionlab.controllers.GreetingController");
     co.edu.escuelaing.reflexionlab.ControllerLoader.load(controllerClass);
     java.lang.reflect.Method method = co.edu.escuelaing.reflexionlab.ControllerLoader.services.get("/greeting");
     Object instance = co.edu.escuelaing.reflexionlab.ControllerLoader.controllerInstances.get("/greeting");
     String result = (String) method.invoke(instance, "Test");
     assertEquals("Hola Test", result);
}
```

## Despliegue en AWS

1. Se empaqueto la aplicación:
   ```
   mvn clean package
   ```
2. Se subió el archivo JAR a una instancia EC2 usando SCP.
3. Se Instala Java en la instancia:
   ```
   sudo yum install java-1.8.0-openjdk
   ```
4. Luego se ejecuta el servidor:
   ```
   java -cp target/classes co.edu.escuelaing.reflexionlab.MicroSpringBoot
   ```
5. Esto abre el puerto 35000 en el Security Group de la instancia EC2.
6. Acceder desde el navegador usando la IP pública de la instancia:

## Calidad y diseño

- El framework explora el classpath y carga todos los controladores anotados.
- Soporta anotaciones `@RestController`, `@GetMapping` y `@RequestParam`.
- El diseño está documentado en este README.
- El repositorio puede ser clonado y ejecutado fácilmente.
