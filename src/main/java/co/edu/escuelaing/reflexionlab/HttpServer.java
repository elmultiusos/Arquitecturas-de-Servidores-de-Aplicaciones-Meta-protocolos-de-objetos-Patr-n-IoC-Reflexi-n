package co.edu.escuelaing.reflexionlab;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    public static void start() throws Exception {
        ServerSocket serverSocket = new ServerSocket(35000);
        System.out.println("Servidor iniciado en puerto 35000");
        while (true) {
            Socket client = serverSocket.accept();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));
            String requestLine = in.readLine();
            if (requestLine == null) {
                client.close();
                continue;
            }
            String fullPath = requestLine.split(" ")[1];
            String path = fullPath.split("\\?")[0];
            String query = "";
            if (fullPath.contains("?")) {
                query = fullPath.split("\\?")[1];
            }
            Method service = ControllerLoader.services.get(path);
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            if (service != null) {
                Object controllerInstance = ControllerLoader.controllerInstances.get(path);
                Object response = null;
                if (service.getParameterCount() == 0) {
                    response = service.invoke(controllerInstance);
                } else {
                    // Extraer parámetros de consulta
                    String[] queryParams = query.split("&");
                    java.util.Map<String, String> paramMap = new java.util.HashMap<>();
                    for (String param : queryParams) {
                        if (param.contains("=")) {
                            String[] kv = param.split("=");
                            paramMap.put(kv[0], kv[1]);
                        }
                    }
                    java.lang.reflect.Parameter[] parameters = service.getParameters();
                    Object[] args = new Object[parameters.length];
                    for (int i = 0; i < parameters.length; i++) {
                        if (parameters[i].isAnnotationPresent(co.edu.escuelaing.reflexionlab.annotations.RequestParam.class)) {
                            co.edu.escuelaing.reflexionlab.annotations.RequestParam req = parameters[i].getAnnotation(co.edu.escuelaing.reflexionlab.annotations.RequestParam.class);
                            String value = paramMap.getOrDefault(req.value(), req.defaultValue());
                            args[i] = value;
                        } else {
                            args[i] = null;
                        }
                    }
                    response = service.invoke(controllerInstance, args);
                }
                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: text/html");
                out.println();
                out.println(response);
            } else {
                out.println("HTTP/1.1 404 Not Found");
                out.println();
                out.println("404 NOT FOUND");
            }
            client.close();
        }
    }
}
