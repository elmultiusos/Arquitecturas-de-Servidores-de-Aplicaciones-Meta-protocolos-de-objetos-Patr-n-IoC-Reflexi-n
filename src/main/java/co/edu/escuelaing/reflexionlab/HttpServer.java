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

            String path = requestLine.split(" ")[1];

            Method service = ControllerLoader.services.get(path);

            PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            if (service != null) {

                Object response = service.invoke(ControllerLoader.controllerInstance);

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
