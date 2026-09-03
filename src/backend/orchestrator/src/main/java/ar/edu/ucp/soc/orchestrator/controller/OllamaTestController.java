package ar.edu.ucp.soc.orchestrator.controller;

import ar.edu.ucp.soc.orchestrator.client.OllamaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
@Slf4j
public class OllamaTestController {

    private final OllamaClient ollamaClient;

    @GetMapping("/ollama")
    public ResponseEntity<Map<String, String>> testOllama() {

        String systemPrompt = "Eres un analista de ciberseguridad experto. Respondé siempre en JSON válido.";
        String userMessage = "Analizá esta alerta: SSH Brute Force detectado desde IP 192.168.1.100, 20 intentos fallidos. Respondé con: {\"veredicto\": \"CRITICO o FALSO_POSITIVO\", \"explicacion\": \"tu análisis\", \"confianza\": 0-100}";

        String respuesta = ollamaClient.chat(systemPrompt, userMessage);

        return ResponseEntity.ok(Map.of(
                "modelo", "qwen2.5:14b",
                "respuesta", respuesta
        ));
    }
}