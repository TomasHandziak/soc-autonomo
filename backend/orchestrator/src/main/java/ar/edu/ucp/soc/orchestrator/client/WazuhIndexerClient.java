package ar.edu.ucp.soc.orchestrator.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.netty.http.client.HttpClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class WazuhIndexerClient {

    private final WebClient webClient;

    public WazuhIndexerClient(
            @Value("${soc.wazuh.indexer-url:https://localhost:9200}") String indexerUrl,
            @Value("${soc.wazuh.reader-user:admin}") String user,
            @Value("${soc.wazuh.reader-pass:SecretPassword}") String pass) {

        // Credenciales en Base64 para Basic Auth
        String credentials = Base64.getEncoder()
                .encodeToString((user + ":" + pass).getBytes());

        try {
            // SSL que acepta certificados autofirmados (entorno de desarrollo)
            SslContext sslContext = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();

            HttpClient httpClient = HttpClient.create()
                    .secure(spec -> spec.sslContext(sslContext));

            this.webClient = WebClient.builder()
                    .baseUrl(indexerUrl)
                    .defaultHeader("Authorization", "Basic " + credentials)
                    .defaultHeader("Content-Type", "application/json")
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .codecs(c -> c.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                    .build();

            log.info("WazuhIndexerClient inicializado | url={}", indexerUrl);

        } catch (Exception e) {
            throw new RuntimeException("Error inicializando WazuhIndexerClient", e);
        }
    }

    public String buscarLogsHistoricos(String hostname, String eventId, int rangoHoras) {
        log.info("Buscando logs históricos | hostname={} | eventId={} | rangoHoras={}",
                hostname, eventId, rangoHoras);

        try {
            // Construir query DSL
            Map<String, Object> query = construirQuery(hostname, eventId, rangoHoras);

            Map response = webClient.post()
                    .uri("/wazuh-alerts-*/_search")
                    .bodyValue(query)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            return parsearResultados(response, hostname);

        } catch (Exception e) {
            log.error("Error consultando Wazuh Indexer | error={}", e.getMessage());
            return "Error consultando logs históricos: " + e.getMessage();
        }
    }

    private Map<String, Object> construirQuery(String hostname, String eventId, int rangoHoras) {
        List<Map<String, Object>> mustClauses = new ArrayList<>();

        // Filtrar por hostname
        mustClauses.add(Map.of("match", Map.of("agent.name", hostname)));

        // Filtrar por rango de tiempo
        mustClauses.add(Map.of("range", Map.of(
                "@timestamp", Map.of("gte", "now-" + rangoHoras + "h")
        )));

        // Filtrar por Event ID si se proporciona
        if (eventId != null && !eventId.isBlank()) {
            mustClauses.add(Map.of("match",
                    Map.of("data.win.system.eventID", eventId)));
        }

        return Map.of(
                "size", 10,
                "sort", List.of(Map.of("@timestamp", Map.of("order", "desc"))),
                "query", Map.of("bool", Map.of("must", mustClauses)),
                "_source", List.of(
                        "agent.name", "rule.id", "rule.description",
                        "rule.level", "@timestamp", "data.win.system.eventID"
                )
        );
    }

    private String parsearResultados(Map response, String hostname) {
        try {
            Map hits = (Map) response.get("hits");
            Map total = (Map) hits.get("total");
            int totalCount = ((Number) total.get("value")).intValue();

            if (totalCount == 0) {
                return "No se encontraron logs históricos para el host " + hostname;
            }

            List<Map> alertas = (List<Map>) hits.get("hits");
            StringBuilder resultado = new StringBuilder();
            resultado.append(String.format("Se encontraron %d alertas históricas para %s. ",
                    totalCount, hostname));
            resultado.append("Últimas alertas: ");

            for (Map alerta : alertas) {
                Map source = (Map) alerta.get("_source");
                Map rule = (Map) source.get("rule");
                String timestamp = source.get("@timestamp").toString();
                String level = rule.get("level").toString();
                String description = rule.get("description").toString();

                resultado.append(String.format("[%s] Nivel %s: %s. ",
                        timestamp.substring(0, 19), level, description));
            }

            return resultado.toString();

        } catch (Exception e) {
            log.error("Error parseando resultados del Indexer", e);
            return "Error parseando resultados del Indexer.";
        }
    }
}