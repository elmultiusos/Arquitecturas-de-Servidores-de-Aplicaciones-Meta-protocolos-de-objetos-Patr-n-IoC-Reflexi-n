package Apache.controllers;

import Apache.annotations.GetMapping;
import Apache.annotations.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @GetMapping("/hello")
    public String hello() {
        return "<html><body>"
                + "<h1>Hola Mundo!</h1>"
                + "<p>Este es un servidor web construido con MicroSpringBoot.</p>"
                + "<a href='/greeting?name=Juan'>Ir a /greeting</a>"
                + "</body></html>";
    }
}
