package ar.edu.ucp.soc.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MitreAttackService {

    private final ObjectMapper objectMapper;
    private final Map<String, String> tecnicasPorId = new HashMap<>();
    private final List<Map<String, String>> todasLasTecnicas = new ArrayList<>();

    public MitreAttackService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void cargarDataset() {
        try {
            log.info("Cargando dataset MITRE ATT&CK...");
            ClassPathResource resource = new ClassPathResource("enterprise-attack.json");
            JsonNode root = objectMapper.readTree(resource.getInputStream());
            JsonNode objetos = root.get("objects");

            if (objetos == null || !objetos.isArray()) {
                log.warn("Dataset MITRE ATT&CK no tiene el formato esperado");
                return;
            }

            int count = 0;
            for (JsonNode obj : objetos) {
                String tipo = obj.has("type") ? obj.get("type").asText() : "";
                if (!"attack-pattern".equals(tipo)) continue;

                String id = "";
                if (obj.has("external_references")) {
                    for (JsonNode ref : obj.get("external_references")) {
                        if ("mitre-attack".equals(ref.has("source_name") ?
                                ref.get("source_name").asText() : "")) {
                            id = ref.has("external_id") ? ref.get("external_id").asText() : "";
                            break;
                        }
                    }
                }

                String nombre      = obj.has("name")        ? obj.get("name").asText()        : "";
                String descripcion = obj.has("description") ? obj.get("description").asText() : "";

                String tactica = "";
                if (obj.has("kill_chain_phases") && obj.get("kill_chain_phases").isArray()) {
                    JsonNode fases = obj.get("kill_chain_phases");
                    if (fases.size() > 0) {
                        tactica = fases.get(0).has("phase_name") ?
                                fases.get(0).get("phase_name").asText() : "";
                    }
                }

                if (!id.isEmpty()) {
                    String resumen = String.format("Técnica %s - %s. Táctica: %s. %s",
                            id, nombre, tactica,
                            descripcion.length() > 200 ?
                                    descripcion.substring(0, 200) + "..." : descripcion);
                    tecnicasPorId.put(id.toUpperCase(), resumen);

                    Map<String, String> tecnica = new HashMap<>();
                    tecnica.put("id", id);
                    tecnica.put("nombre", nombre);
                    tecnica.put("tactica", tactica);
                    tecnica.put("descripcion", descripcion.length() > 100 ?
                            descripcion.substring(0, 100) : descripcion);
                    todasLasTecnicas.add(tecnica);
                    count++;
                }
            }

            log.info("MITRE ATT&CK cargado | {} técnicas indexadas", count);

        } catch (Exception e) {
            log.error("Error cargando dataset MITRE ATT&CK", e);
        }
    }

    public String buscarTecnica(String query) {
        if (query == null || query.isBlank()) {
            return "Query vacío";
        }

        String queryUpper = query.toUpperCase().trim();

        // Búsqueda por ID exacto (ej: T1078, T1059.001)
        if (tecnicasPorId.containsKey(queryUpper)) {
            return tecnicasPorId.get(queryUpper);
        }

        // Búsqueda por keyword en nombre y descripción
        String queryLower = query.toLowerCase();
        List<String> resultados = new ArrayList<>();

        for (Map<String, String> tecnica : todasLasTecnicas) {
            String nombre      = tecnica.getOrDefault("nombre", "").toLowerCase();
            String descripcion = tecnica.getOrDefault("descripcion", "").toLowerCase();
            String tactica     = tecnica.getOrDefault("tactica", "").toLowerCase();

            if (nombre.contains(queryLower) ||
                    descripcion.contains(queryLower) ||
                    tactica.contains(queryLower)) {
                resultados.add(String.format("%s - %s (táctica: %s)",
                        tecnica.get("id"),
                        tecnica.get("nombre"),
                        tecnica.get("tactica")));
                if (resultados.size() >= 3) break;
            }
        }

        if (resultados.isEmpty()) {
            return "No se encontraron técnicas MITRE ATT&CK para: " + query;
        }

        return "Técnicas encontradas para '" + query + "': " + String.join(" | ", resultados);
    }
}