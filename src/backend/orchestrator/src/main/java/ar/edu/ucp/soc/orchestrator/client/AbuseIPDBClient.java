package ar.edu.ucp.soc.orchestrator.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

@Component
@Slf4j
public class AbuseIPDBClient {

    private final WebClient webClient;
    private final String apiKey;

    public AbuseIPDBClient(
            @Value("${soc.cti.abuseipdb.api-key:PENDIENTE}") String apiKey) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.abuseipdb.com/api/v2")
                .defaultHeader("Key", apiKey)
                .defaultHeader("Accept", "application/json")
                .build();
        log.info("AbuseIPDBClient inicializado");
    }

    public String consultarIP(String ip) {
        if (apiKey.equals("PENDIENTE")) {
            return "AbuseIPDB no configurado";
        }

        try {
            log.info("Consultando AbuseIPDB | ip={}", ip);

            Map response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/check")
                            .queryParam("ipAddress", ip)
                            .queryParam("maxAgeInDays", "90")
                            .queryParam("verbose", "true")
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            return parsearRespuesta(response, ip);

        } catch (Exception e) {
            log.error("Error consultando AbuseIPDB | ip={} | error={}", ip, e.getMessage());
            return "Error consultando AbuseIPDB: " + e.getMessage();
        }
    }

    private String parsearRespuesta(Map response, String ip) {
        try {
            Map data = (Map) response.get("data");

            int abuseScore  = data.get("abuseConfidenceScore") != null ?
                    ((Number) data.get("abuseConfidenceScore")).intValue() : 0;
            int totalReports = data.get("totalReports") != null ?
                    ((Number) data.get("totalReports")).intValue() : 0;
            String country  = data.get("countryCode") != null ?
                    data.get("countryCode").toString() : "desconocido";
            String isp      = data.get("isp") != null ?
                    data.get("isp").toString() : "desconocido";
            boolean isWhitelisted = data.get("isWhitelisted") != null &&
                    (Boolean) data.get("isWhitelisted");

            String nivel = abuseScore >= 80 ? "ALTO RIESGO" :
                    abuseScore >= 40 ? "RIESGO MEDIO" :
                    abuseScore >= 10 ? "RIESGO BAJO" : "LIMPIA";

            return String.format(
                    "IP %s: Score de abuso=%d/100, %d reportes, País=%s, ISP=%s, " +
                            "Whitelisted=%s. NIVEL DE RIESGO: %s.",
                    ip, abuseScore, totalReports, country, isp,
                    isWhitelisted ? "Sí" : "No", nivel);

        } catch (Exception e) {
            return "IP " + ip + ": No se pudo parsear la respuesta de AbuseIPDB.";
        }
    }
}