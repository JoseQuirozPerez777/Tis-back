package com.teamsys.portafolios.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Map;
import java.util.List;

@Service
public class EmailService {

    @Value("${brevo.api.key:}")
    private String apiKey;

    @Value("${brevo.email.from:no-reply@example.com}")
    private String from;

    private final WebClient webClient;

    public EmailService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.brevo.com/v3/smtp/email").build();
    }

    public void enviarCodigoRecuperacion(String emailDestino, String codigo) {
        if (apiKey == null || apiKey.isEmpty()) {
            imprimirFallback(emailDestino, codigo, "MODO DEV - API KEY NO CONFIGURADA");
            return;
        }

        Map<String, Object> body = Map.of(
                "sender", Map.of("name", "TeamSys", "email", "no-reply@teamsys.com"),
                "to", List.of(Map.of("email", emailDestino)),
                "subject", "🔗 Tu código de recuperación - TeamSys",
                "htmlContent", getHtmlTemplate(codigo)
        );

        this.webClient.post()
                .header("api-key", apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(response -> System.out.println("✅ Email enviado con Brevo a: " + emailDestino))
                .doOnError(error -> imprimirFallback(emailDestino, codigo, "ERROR API BREVO: " + error.getMessage()))
                .subscribe(); // Ejecución asíncrona para no bloquear al usuario
    }

    private void imprimirFallback(String email, String codigo, String motivo) {
        System.out.println("\n📧 --- FALLBACK EMAIL (" + motivo + ") ---");
        System.out.println("Para: " + email);
        System.out.println("Código: " + codigo);
        System.out.println("-------------------------------------------\n");
    }

    private String getHtmlTemplate(String codigo) {
        return "<html><body>" +
                "<h2>Hola,</h2>" +
                "<p>Tu código de seguridad para TeamSys es: <strong>" + codigo + "</strong></p>" +
                "<p>Este código expira en 2 minutos.</p>" +
                "</body></html>";
    }
}