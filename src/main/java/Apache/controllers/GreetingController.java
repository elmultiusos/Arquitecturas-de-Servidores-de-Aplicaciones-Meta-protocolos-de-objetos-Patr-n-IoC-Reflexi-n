package Apache.controllers;

import java.util.concurrent.atomic.AtomicLong;

import Apache.annotations.GetMapping;
import Apache.annotations.RequestParam;
import Apache.annotations.RestController;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hola " + name;
    }
}
