package services;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class VerificationServer {

    private HttpServer server;
    private final ServiceUsers serviceUsers;

    public VerificationServer(ServiceUsers serviceUsers) {
        this.serviceUsers = serviceUsers;
    }

    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/verify", new VerifyHandler());
        server.setExecutor(null); // executor simple par défaut
        server.start();
        System.out.println("Serveur de vérification démarré sur port " + port);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Serveur de vérification arrêté");
        }
    }

    private class VerifyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response;
            int statusCode = 200;

            String method = exchange.getRequestMethod();
            if ("GET".equals(method)) {
                String query = exchange.getRequestURI().getQuery();
                String token = null;

                if (query != null && query.contains("token=")) {
                    String[] parts = query.split("token=");
                    if (parts.length > 1) {
                        token = parts[1].split("&")[0]; // prend juste le token
                    }
                }

                if (token != null && !token.isBlank()) {
                    boolean success = serviceUsers.verifyUser(token);
                    if (success) {
                        response = getSuccessHtml();
                    } else {
                        statusCode = 400;
                        response = getErrorHtml("Lien invalide ou expiré.");
                    }
                } else {
                    statusCode = 400;
                    response = getErrorHtml("Token manquant dans l'URL.");
                }
            } else {
                statusCode = 405;
                response = "Méthode non autorisée (utilisez GET)";
            }

            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(statusCode, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
    }

    private String getSuccessHtml() {
        return """
            <!DOCTYPE html>
            <html lang="fr">
            <head>
                <meta charset="UTF-8">
                <title>Compte activé - After Travel</title>
                <style>
                    body { font-family: Arial, sans-serif; text-align: center; padding: 80px; background: #f8f9fa; }
                    h1 { color: #27ae60; }
                    p { font-size: 1.2em; margin: 20px 0; }
                    .btn { display: inline-block; padding: 12px 30px; background: #3498db; color: white; text-decoration: none; border-radius: 5px; font-weight: bold; }
                    .btn:hover { background: #2980b9; }
                </style>
            </head>
            <body>
                <h1>Compte activé avec succès !</h1>
                <p>Votre compte After Travel est maintenant actif.</p>
                <p>Vous pouvez fermer cette page et vous connecter dans l'application.</p>
                <a href="javascript:window.close()" class="btn">Fermer la fenêtre</a>
            </body>
            </html>
            """;
    }

    private String getErrorHtml(String message) {
        return """
            <!DOCTYPE html>
            <html lang="fr">
            <head>
                <meta charset="UTF-8">
                <title>Erreur - After Travel</title>
                <style>
                    body { font-family: Arial, sans-serif; text-align: center; padding: 80px; background: #fff5f5; }
                    h1 { color: #c0392b; }
                    p { font-size: 1.2em; color: #e74c3c; }
                </style>
            </head>
            <body>
                <h1>Erreur de vérification</h1>
                <p>%s</p>
                <p>Le lien est invalide, expiré ou déjà utilisé.<br>Essayez de vous réinscrire.</p>
            </body>
            </html>
            """.formatted(message);
    }
}