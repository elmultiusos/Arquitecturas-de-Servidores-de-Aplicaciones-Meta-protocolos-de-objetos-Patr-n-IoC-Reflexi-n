package co.edu.escuelaing.reflexionlab.controllers;

import co.edu.escuelaing.reflexionlab.annotations.GetMapping;
import co.edu.escuelaing.reflexionlab.annotations.RestController;

@RestController
public class GreetingController {

    @GetMapping("/greeting")
    public String greeting(@co.edu.escuelaing.reflexionlab.annotations.RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hola " + name;
    }
}
