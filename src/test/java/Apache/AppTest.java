package Apache;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Apache.controllers.GreetingController;
import Apache.controllers.HelloController;
import Apache.server.MicroSpringBoot;

/**
 * Tests para el framework MicroSpringBoot.
 */
public class AppTest {

    private MicroSpringBoot framework;

    @BeforeEach
    public void setUp() throws Exception {
        framework = new MicroSpringBoot();
        framework.registerControllerClass(HelloController.class);
        framework.registerControllerClass(GreetingController.class);
    }

    @Test
    public void testRoutesRegistered() {
        assertTrue(framework.getRegisteredRoutes().contains("/"));
        assertTrue(framework.getRegisteredRoutes().contains("/hello"));
        assertTrue(framework.getRegisteredRoutes().contains("/greeting"));
    }

    @Test
    public void testHelloControllerIndex() {
        String response = framework.handleRequest("/", new HashMap<>());
        assertNotNull(response);
        assertEquals("Greetings from Spring Boot!", response);
    }

    @Test
    public void testHelloControllerHello() {
        String response = framework.handleRequest("/hello", new HashMap<>());
        assertNotNull(response);
        assertTrue(response.contains("Hola Mundo!"));
    }

    @Test
    public void testGreetingWithDefaultParam() {
        String response = framework.handleRequest("/greeting", new HashMap<>());
        assertNotNull(response);
        assertEquals("Hola World", response);
    }

    @Test
    public void testGreetingWithCustomParam() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "Juan");
        String response = framework.handleRequest("/greeting", params);
        assertNotNull(response);
        assertEquals("Hola Juan", response);
    }

    @Test
    public void testUnknownRouteReturnsNull() {
        String response = framework.handleRequest("/nonexistent", new HashMap<>());
        assertNull(response);
    }

    @Test
    public void testRegisterByClassName() throws Exception {
        MicroSpringBoot fw = new MicroSpringBoot();
        fw.registerController("Apache.controllers.HelloController");
        assertTrue(fw.getRegisteredRoutes().contains("/"));
    }
}
