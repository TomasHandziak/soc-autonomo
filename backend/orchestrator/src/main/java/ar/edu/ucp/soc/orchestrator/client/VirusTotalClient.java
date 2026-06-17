package ar.edu.ucp.soc.orchestrator.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

@Component
@Slf4j
public class VirusTotalClient {

    private final WebClient webClient;
    private final String apiKey;

    public VirusTotalClient(
            @Value("${soc.cti.virustotal.api-key:PENDIENTE}") String apiKey) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl("https://www.virustotal.com/api/v3")
                .defaultHeader("x-apikey", apiKey)
                .codecs(c -> c.defaultCodecs().maxInMemorySize(5 * 1024 * 1024))
                .build();
        log.info("VirusTotalClient inicializado");
    }

    public String analizarHash(String hash) {
        if (apiKey.equals("PENDIENTE")) {
            return "VirusTotal no configurado";
        }

        try {
            log.info("Consultando VirusTotal | hash={}", hash);

            Map response = webClient.get()
                    .uri("/files/{hash}", hash)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(15))
                    .block();

            return parsearRespuesta(response, hash);

        } catch (Exception e) {
            log.error("Error consultando VirusTotal | hash={} | error={}", hash, e.getMessage());
            return "Error consultando VirusTotal: " + e.getMessage();
        }
    }

    private String parsearRespuesta(Map response, String hash) {
        try {
            Map data = (Map) response.get("data");
            Map attributes = (Map) ((Map) data).get("attributes");
            Map stats = (Map) attributes.get("last_analysis_stats");

            int malicious  = stats.get("malicious")  != null ? ((Number) stats.get("malicious")).intValue()  : 0;
            int suspicious = stats.get("suspicious")  != null ? ((Number) stats.get("suspicious")).intValue() : 0;
            int harmless   = stats.get("harmless")    != null ? ((Number) stats.get("harmless")).intValue()   : 0;
            int undetected = stats.get("undetected")  != null ? ((Number) stats.get("undetected")).intValue() : 0;
            int total      = malicious + suspicious + harmless + undetected;

            String nombre = attributes.get("meaningful_name") != null ?
                    attributes.get("meaningful_name").toString() : "desconocido";

            String veredicto = malicious > 0 ? "MALICIOSO" :
                    suspicious > 0 ? "SOSPECHOSO" : "LIMPIO";

            return String.format(
                    "Hash %s: %d/%d vendors detectan como malicioso. " +
                            "Sospechosos: %d. Nombre: %s. VEREDICTO: %s.",
                    hash, malicious, total, suspicious, nombre, veredicto);

        } catch (Exception e) {
            return "Hash " + hash + ": No se pudo parsear la respuesta de VirusTotal.";
        }
    }
}