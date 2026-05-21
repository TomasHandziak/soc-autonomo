package ar.edu.ucp.soc.orchestrator.service;

import ar.edu.ucp.soc.orchestrator.model.AlertEvent;
import ar.edu.ucp.soc.orchestrator.model.Investigacion;
import ar.edu.ucp.soc.orchestrator.repository.InvestigacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final InvestigacionRepository investigacionRepository;
    private final InvestigationEngine investigationEngine;

    public Investigacion procesarAlerta(AlertEvent alerta) {

        log.info("Alerta recibida | agente={} | regla={} | nivel={}",
                alerta.getAgentName(), alerta.getRuleName(), alerta.getRuleLevel());

        // Crear la investigación en estado RECIBIDA
        Investigacion investigacion = Investigacion.builder()
                .id(UUID.randomUUID().toString())
                .alertaId(alerta.getId())
                .agenteName(alerta.getAgentName())
                .ruleName(alerta.getRuleName())
                .ruleLevel(alerta.getRuleLevel())
                .estado(Investigacion.Estado.RECIBIDA)
                .iteraciones(0)
                .timestampIngesta(LocalDateTime.now())
                .soarDetonado(false)
                .aprobadaPorHumano(null)
                .build();

        // Guardar en base de datos
        investigacionRepository.save(investigacion);

        // Lanzar la investigación de forma asíncrona
        // El controller responde 202 inmediatamente y la IA trabaja en segundo plano
        investigarAsync(investigacion, alerta);

        log.info("Investigación encolada | id={}", investigacion.getId());
        return investigacion;
    }

    @Async
    public void investigarAsync(Investigacion investigacion, AlertEvent alerta) {
        try {
            investigationEngine.investigar(investigacion, alerta);
        } catch (Exception e) {
            log.error("Error en investigación asíncrona | id={}", investigacion.getId(), e);
        }
    }

    public Investigacion obtenerPorId(String id) {
        return investigacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Investigación no encontrada: " + id));
    }

    public List<Investigacion> obtenerPendientesRevision() {
        return investigacionRepository.findByEstado(Investigacion.Estado.PENDIENTE_REVISION_HUMANA);
    }

    public List<Investigacion> obtenerTodas() {
        return investigacionRepository.findAll();
    }
}