package ar.edu.ucp.soc.orchestrator.controller;

import ar.edu.ucp.soc.orchestrator.model.AlertEvent;
import ar.edu.ucp.soc.orchestrator.model.Investigacion;
import ar.edu.ucp.soc.orchestrator.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
@Slf4j
public class AlertController {

    private final AlertService alertService;

    @PostMapping("/ingest")
    public ResponseEntity<Map<String, String>> ingestAlert(
            @RequestBody Map<String, Object> payload) {

        log.info("Alerta recibida desde Wazuh | payload={}", payload);

        // Mapear el payload de Wazuh a AlertEvent
        AlertEvent alerta = AlertEvent.builder()
                .id(UUID.randomUUID().toString())
                .agentId(getStr(payload, "agent_id"))
                .agentName(getStr(payload, "agent_name"))
                .ruleId(getStr(payload, "rule_id"))
                .ruleName(getStr(payload, "rule_name"))
                .ruleLevel(getInt(payload, "rule_level"))
                .description(getStr(payload, "description"))
                .sourceIp(getStr(payload, "source_ip"))
                .rawLog(payload.toString())
                .severity(AlertEvent.fromWazuhLevel(getInt(payload, "rule_level")))
                .build();

        // Procesar la alerta y crear la investigación
        Investigacion investigacion = alertService.procesarAlerta(alerta);

        return ResponseEntity.accepted()
                .body(Map.of(
                        "correlationId", investigacion.getId(),
                        "status", investigacion.getEstado().toString(),
                        "agente", alerta.getAgentName() != null ? alerta.getAgentName() : "desconocido"
                ));
    }

    @GetMapping("/investigaciones/{id}")
    public ResponseEntity<Investigacion> obtenerInvestigacion(@PathVariable String id) {
        return ResponseEntity.ok(alertService.obtenerPorId(id));
    }

    // Helpers para extraer valores del payload JSON
    private String getStr(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : null;
    }

    private int getInt(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val == null) return 0;
        try { return Integer.parseInt(val.toString()); }
        catch (NumberFormatException e) { return 0; }
    }
}