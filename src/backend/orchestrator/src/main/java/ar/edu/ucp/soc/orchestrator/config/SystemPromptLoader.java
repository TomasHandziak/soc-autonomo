package ar.edu.ucp.soc.orchestrator.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@Getter
public class SystemPromptLoader {

    private String systemPrompt;

    @PostConstruct
    public void cargarPrompt() {
        try {
            ClassPathResource resource = new ClassPathResource("system_prompt.txt");
            systemPrompt = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            log.info("System prompt cargado correctamente | chars={}", systemPrompt.length());
        } catch (IOException e) {
            log.error("Error cargando system_prompt.txt", e);
            systemPrompt = "Eres un analista de ciberseguridad. Analizá la alerta y respondé en JSON.";
        }
    }
}