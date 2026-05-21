package ar.edu.ucp.soc.orchestrator.controller;

import ar.edu.ucp.soc.orchestrator.model.AlertEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/alerts")
@Slf4j
public class AlertController {

    @PostMapping("/ingest")
    public ResponseEntity<Map<String, String>> ingestAlert(
            @RequestBody Map<String, Object> payload) {

        String correlationId = UUID.randomUUID().toString();

        log.info("Alerta recibida | correlationId={} | payload={}",
                correlationId, payload);

        // Por ahora solo logueamos y respondemos 202 Accepted
        // En el próximo paso conectamos con el servicio de investigación
        return ResponseEntity.accepted()
                .body(Map.of(
                        "correlationId", correlationId,
                        "status", "RECIBIDA",
                        "timestamp", LocalDateTime.now().toString()
                ));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "Alert endpoint operativo"));
    }
}