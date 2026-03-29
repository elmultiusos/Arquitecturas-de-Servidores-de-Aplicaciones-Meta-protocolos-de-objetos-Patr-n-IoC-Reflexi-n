package Apache.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Servidor web HTTP simple que sirve archivos estaticos (HTML, PNG, CSS, JS) y
 * delega rutas dinamicas al framework IoC (MicroSpringBoot).
 */
public class SimpleWebServer {

    private final int port;
    private final String staticFilesRoot;
    private final MicroSpringBoot framework;

    public SimpleWebServer(int port, String staticFilesRoot, MicroSpringBoot framework) {
        this.port = port;
        this.staticFilesRoot = staticFilesRoot;
        this.framework = framework;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor iniciado en el puerto " + port);
            System.out.println("Archivos estaticos desde: " + Paths.get(staticFilesRoot).toAbsolutePath());
            System.out.println("Abra http://localhost:" + port + " en su navegador");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            }
        }
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); OutputStream out = clientSocket.getOutputStream()) {

            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                clientSocket.close();
                return;
            }

            System.out.println("Request: " + requestLine);

            String[] parts = requestLine.split(" ");
            if (parts.length < 2) {
                clientSocket.close();
                return;
            }

            String method = parts[0];
            String fullPath = parts[1];

            // Separar path y query string
            String path;
            String queryString = "";
            int questionMark = fullPath.indexOf('?');
            if (questionMark != -1) {
                path = fullPath.substring(0, questionMark);
                queryString = fullPath.substring(questionMark + 1);
            } else {
                path = fullPath;
            }

            // Consumir headers
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                // solo consumir
            }

            if (!"GET".equalsIgnoreCase(method)) {
                sendError(out, 405, "Method Not Allowed");
                return;
            }

            // Parsear query params
            Map<String, String> queryParams = parseQueryParams(queryString);

            // Intentar servir con el framework IoC primero
            String frameworkResponse = framework.handleRequest(path, queryParams);
            if (frameworkResponse != null) {
                sendHtmlResponse(out, 200, frameworkResponse);
                return;
            }

            // Servir archivo estatico
            serveStaticFile(out, path);

        } catch (IOException e) {
            System.err.println("Error al manejar cliente: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                // ignorar
            }
        }
    }

    private Map<String, String> parseQueryParams(String queryString) {
        Map<String, String> params = new HashMap<>();
        if (queryString == null || queryString.isEmpty()) {
            return params;
        }
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            int eq = pair.indexOf('=');
            if (eq > 0) {
                String key = URLDecoder.decode(pair.substring(0, eq), StandardCharsets.UTF_8);
                String value = URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        return params;
    }

    private void serveStaticFile(OutputStream out, String path) throws IOException {
        if ("/".equals(path)) {
            path = "/index.html";
        }

        // Prevenir path traversal
        String normalizedPath = Paths.get(path).normalize().toString().replace("\\", "/");
        if (normalizedPath.startsWith("..") || normalizedPath.contains("/../")) {
            sendError(out, 403, "Forbidden");
            return;
        }

        Path filePath = Paths.get(staticFilesRoot, normalizedPath);
        if (!filePath.normalize().startsWith(Paths.get(staticFilesRoot).normalize())) {
            sendError(out, 403, "Forbidden");
            return;
        }

        if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
            sendError(out, 404, "Not Found");
            return;
        }

        String contentType = getContentType(filePath.toString());
        byte[] fileBytes = Files.readAllBytes(filePath);

        String header = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: " + contentType + "\r\n"
                + "Content-Length: " + fileBytes.length + "\r\n"
                + "\r\n";

        out.write(header.getBytes(StandardCharsets.UTF_8));
        out.write(fileBytes);
        out.flush();
    }

    private void sendHtmlResponse(OutputStream out, int statusCode, String body) throws IOException {
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        String header = "HTTP/1.1 " + statusCode + " OK\r\n"
                + "Content-Type: text/html; charset=UTF-8\r\n"
                + "Content-Length: " + bodyBytes.length + "\r\n"
                + "\r\n";
        out.write(header.getBytes(StandardCharsets.UTF_8));
        out.write(bodyBytes);
        out.flush();
    }

    private void sendError(OutputStream out, int statusCode, String message) throws IOException {
        String body = "<html><body><h1>" + statusCode + " " + message + "</h1></body></html>";
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        String header = "HTTP/1.1 " + statusCode + " " + message + "\r\n"
                + "Content-Type: text/html; charset=UTF-8\r\n"
                + "Content-Length: " + bodyBytes.length + "\r\n"
                + "\r\n";
        out.write(header.getBytes(StandardCharsets.UTF_8));
        out.write(bodyBytes);
        out.flush();
    }

    private String getContentType(String fileName) {
        if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
            return "text/html; charset=UTF-8";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else if (fileName.endsWith(".js")) {
            return "application/javascript";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else if (fileName.endsWith(".ico")) {
            return "image/x-icon";
        } else if (fileName.endsWith(".json")) {
            return "application/json";
        } else {
            return "application/octet-stream";
        }
    }
}
