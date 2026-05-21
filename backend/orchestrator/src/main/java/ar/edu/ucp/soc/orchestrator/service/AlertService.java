package ar.edu.ucp.soc.orchestrator.service;

import ar.edu.ucp.soc.orchestrator.model.AlertEvent;
import ar.edu.ucp.soc.orchestrator.model.Investigacion;
import ar.edu.ucp.soc.orchestrator.repository.InvestigacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final InvestigacionRepository investigacionRepository;

    public Investigacion procesarAlerta(AlertEvent alerta) {

        log.info("Procesando alerta | agente={} | regla={} | nivel={}",
                alerta.getAgentName(),
                alerta.getRuleName(),
                alerta.getRuleLevel());

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
        Investigacion guardada = investigacionRepository.save(investigacion);

        log.info("Investigacion creada | id={} | estado={}",
                guardada.getId(),
                guardada.getEstado());

        return guardada;
    }

    public Investigacion obtenerPorId(String id) {
        return investigacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Investigacion no encontrada: " + id));
    }
}