package com.teamsys.portafolios.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;
import java.util.List;

@Service
public class EmailService {

    @Value("${brevo.api.key:}")
    private String apiKey;

    @Value("${brevo.email.from}")
    private String from;

    private final WebClient webClient;

    public EmailService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.brevo.com/v3/smtp/email").build();
    }

    /**
     * Envía un correo con un enlace de recuperación basado en JWT.
     * No requiere guardar nada en base de datos.
     */
    public void enviarEnlaceRecuperacion(String emailDestino, String urlEnlace) {
        // Validación de seguridad por si la API Key no cargó
        if (apiKey == null || apiKey.isEmpty()) {
            imprimirFallback(emailDestino, urlEnlace, "API KEY NO CONFIGURADA");
            return;
        }

        // Construcción del cuerpo para la API de Brevo
        Map<String, Object> body = Map.of(
                "sender", Map.of("name", "TeamSys", "email", this.from),
                "to", List.of(Map.of("email", emailDestino)),
                "subject", "🔑 Restablecer tu contraseña - TeamSys",
                "htmlContent", getHtmlTemplateReset(urlEnlace)
        );

        // Petición asíncrona a Brevo
        this.webClient.post()
                .header("api-key", apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(response -> System.out.println("✅ Enlace de recuperación enviado a: " + emailDestino))
                .doOnError(error -> {
                    System.err.println("❌ Error al enviar correo con Brevo: " + error.getMessage());
                    imprimirFallback(emailDestino, urlEnlace, "ERROR EN PETICIÓN API");
                })
                .subscribe(); // No bloquea el hilo principal
    }

    /**
     * Diseño del correo en HTML con un botón centrado.
     */
    private String getHtmlTemplateReset(String url) {
        return "<html>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "  <div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;'>" +
                "    <h2 style='color: #007bff; text-align: center;'>Recuperación de Contraseña</h2>" +
                "    <p>Hola,</p>" +
                "    <p>Has solicitado restablecer tu contraseña en <strong>TeamSys</strong>. Haz clic en el botón de abajo para completar el proceso:</p>" +
                "    <div style='text-align: center; margin: 30px 0;'>" +
                "      <a href='" + url + "' style='background-color: #007bff; color: white; padding: 15px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;'>Restablecer Contraseña</a>" +
                "    </div>" +
                "    <p>Si el botón no funciona, puedes copiar y pegar el siguiente enlace en tu navegador:</p>" +
                "    <p style='word-break: break-all; color: #666; font-size: 12px;'>" + url + "</p>" +
                "    <hr style='border: 0; border-top: 1px solid #eee; margin: 20px 0;'>" +
                "    <p style='font-size: 12px; color: #999; text-align: center;'>Este enlace es válido por tiempo limitado. Si no solicitaste este cambio, puedes ignorar este correo.</p>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }

    /**
     * Imprime en consola la información en caso de que el envío falle (útil para desarrollo).
     */
    private void imprimirFallback(String email, String url, String motivo) {
        System.out.println("\n⚠️ --- FALLBACK EMAIL (" + motivo + ") ---");
        System.out.println("DESTINATARIO: " + email);
        System.out.println("URL GENERADA: " + url);
        System.out.println("----------------------------------------------\n");
    }
}