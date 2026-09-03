package ar.edu.ucp.soc.orchestrator.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class OllamaClient {

    private final WebClient webClient;
    private final String model;

    public OllamaClient(
            @Value("${spring.ai.ollama.base-url:http://localhost:11434}") String baseUrl,
            @Value("${spring.ai.ollama.chat.model:qwen2.5:14b}") String model) {
        this.model = model;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .codecs(c -> c.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build();
        log.info("OllamaClient inicializado | url={} | modelo={}", baseUrl, model);
    }

    public String chat(String systemPrompt, String userMessage) {
        log.debug("Llamando al LLM | modelo={} | mensaje={}", model,
                userMessage.substring(0, Math.min(100, userMessage.length())));

        long inicio = System.currentTimeMillis();

        Map<String, Object> body = Map.of(
                "model", model,
                "stream", false,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user",   "content", userMessage)
                )
        );

        Map response = webClient.post()
                .uri("/api/chat")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(120))
                .block();

        long duracion = System.currentTimeMillis() - inicio;

        String contenido = extraerContenido(response);
        log.info("Respuesta del LLM recibida | duracion={}ms | chars={}",
                duracion, contenido.length());

        return contenido;
    }

    private String extraerContenido(Map response) {
        if (response == null) return "";
        try {
            Map message = (Map) response.get("message");
            return message != null ? (String) message.get("content") : "";
        } catch (Exception e) {
            log.error("Error extrayendo contenido de respuesta Ollama", e);
            return "";
        }
    }
}