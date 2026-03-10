package co.edu.escuelaing.reflexionlab;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testControllerLoader() throws Exception {
        Class<?> controllerClass = Class.forName("co.edu.escuelaing.reflexionlab.controllers.GreetingController");
        co.edu.escuelaing.reflexionlab.ControllerLoader.load(controllerClass);
        java.lang.reflect.Method method = co.edu.escuelaing.reflexionlab.ControllerLoader.services.get("/greeting");
        Object instance = co.edu.escuelaing.reflexionlab.ControllerLoader.controllerInstances.get("/greeting");
        String result = (String) method.invoke(instance, "Test");
        assertEquals("Hola Test", result);
    }

    public void testHttpServerMapping() throws Exception {
        Class<?> controllerClass = Class.forName("co.edu.escuelaing.reflexionlab.controllers.GreetingController");
        co.edu.escuelaing.reflexionlab.ControllerLoader.load(controllerClass);
        java.lang.reflect.Method method = co.edu.escuelaing.reflexionlab.ControllerLoader.services.get("/greeting");
        assertNotNull(method);
    }
}
