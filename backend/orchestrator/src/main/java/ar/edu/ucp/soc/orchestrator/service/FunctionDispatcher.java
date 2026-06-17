package ar.edu.ucp.soc.orchestrator.service;

import ar.edu.ucp.soc.orchestrator.client.AbuseIPDBClient;
import ar.edu.ucp.soc.orchestrator.client.VirusTotalClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FunctionDispatcher {

    private final VirusTotalClient virusTotalClient;
    private final AbuseIPDBClient abuseIPDBClient;
    private final MitreAttackService mitreAttackService;
    private final ObjectMapper objectMapper;

    public String ejecutar(String respuestaLLM) {
        try {
            String json = respuestaLLM.trim()
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            JsonNode nodo = objectMapper.readTree(json);
            String herramienta = nodo.has("herramienta") ?
                    nodo.get("herramienta").asText() : "";
            JsonNode parametros = nodo.get("parametros");

            log.info("Ejecutando herramienta: {}", herramienta);

            return switch (herramienta) {
                case "consultar_virustotal" -> {
                    String hash = parametros != null && parametros.has("hash") ?
                            parametros.get("hash").asText() : "";
                    if (hash.isBlank()) yield "Error: hash no proporcionado";
                    yield virusTotalClient.analizarHash(hash);
                }
                case "consultar_abuseipdb" -> {
                    String ip = parametros != null && parametros.has("ip") ?
                            parametros.get("ip").asText() : "";
                    if (ip.isBlank()) yield "Error: IP no proporcionada";
                    yield abuseIPDBClient.consultarIP(ip);
                }
                case "buscar_tactica_mitre" -> {
                    String query = parametros != null && parametros.has("query") ?
                            parametros.get("query").asText() : "";
                    if (query.isBlank()) yield "Error: query no proporcionado";
                    yield mitreAttackService.buscarTecnica(query);
                }
                case "buscar_logs_historicos" -> {
                    // Placeholder hasta implementar WazuhIndexerClient
                    String hostname = parametros != null && parametros.has("hostname") ?
                            parametros.get("hostname").asText() : "desconocido";
                    yield "Búsqueda de logs históricos para " + hostname +
                            ": funcionalidad pendiente de implementación (Sprint 3).";
                }
                case "buscar_alertas_previas" -> {
                    String agentId = parametros != null && parametros.has("agent_id") ?
                            parametros.get("agent_id").asText() : "desconocido";
                    yield "Búsqueda de alertas previas para agente " + agentId +
                            ": funcionalidad pendiente de implementación (Sprint 3).";
                }
                default -> "Herramienta desconocida: " + herramienta +
                        ". Herramientas disponibles: consultar_virustotal, " +
                        "consultar_abuseipdb, buscar_tactica_mitre, " +
                        "buscar_logs_historicos, buscar_alertas_previas.";
            };

        } catch (Exception e) {
            log.error("Error ejecutando herramienta | error={}", e.getMessage());
            return "Error ejecutando herramienta: " + e.getMessage();
        }
    }
}