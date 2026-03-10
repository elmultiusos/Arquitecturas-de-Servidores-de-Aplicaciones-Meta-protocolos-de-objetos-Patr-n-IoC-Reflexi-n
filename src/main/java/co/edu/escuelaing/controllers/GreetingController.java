package co.edu.escuelaing.reflexionlab.controllers;

import co.edu.escuelaing.reflexionlab.annotations.GetMapping;
import co.edu.escuelaing.reflexionlab.annotations.RestController;

@RestController
public class GreetingController {

    @GetMapping("/greeting")
    public String greeting() {
        return "Hola Mundo desde MicroSpringBoot";
    }
}
