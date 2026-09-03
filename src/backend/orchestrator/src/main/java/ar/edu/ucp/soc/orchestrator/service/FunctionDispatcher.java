package ar.edu.ucp.soc.orchestrator.service;

import ar.edu.ucp.soc.orchestrator.client.AbuseIPDBClient;
import ar.edu.ucp.soc.orchestrator.client.VirusTotalClient;
import ar.edu.ucp.soc.orchestrator.client.WazuhIndexerClient;
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
    private final WazuhIndexerClient wazuhIndexerClient;
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
                    String hostname = parametros != null && parametros.has("hostname") ?
                            parametros.get("hostname").asText() : "";
                    String eventId = parametros != null && parametros.has("event_id") ?
                            parametros.get("event_id").asText() : null;
                    int rangoHoras = parametros != null && parametros.has("rango_horas") ?
                            parametros.get("rango_horas").asInt() : 24;
                    if (hostname.isBlank()) yield "Error: hostname no proporcionado";
                    yield wazuhIndexerClient.buscarLogsHistoricos(hostname, eventId, rangoHoras);
                }
                case "buscar_alertas_previas" -> {
                    String hostname = parametros != null && parametros.has("hostname") ?
                            parametros.get("hostname").asText() : "";
                    int rangoHoras = parametros != null && parametros.has("rango_horas") ?
                            parametros.get("rango_horas").asInt() : 24;
                    if (hostname.isBlank()) yield "Error: hostname no proporcionado";
                    yield wazuhIndexerClient.buscarLogsHistoricos(hostname, null, rangoHoras);
                }
                default -> "Herramienta desconocida: " + herramienta +
                        ". Disponibles: consultar_virustotal, consultar_abuseipdb, " +
                        "buscar_tactica_mitre, buscar_logs_historicos, buscar_alertas_previas.";
            };

        } catch (Exception e) {
            log.error("Error ejecutando herramienta | error={}", e.getMessage());
            return "Error ejecutando herramienta: " + e.getMessage();
        }
    }
}