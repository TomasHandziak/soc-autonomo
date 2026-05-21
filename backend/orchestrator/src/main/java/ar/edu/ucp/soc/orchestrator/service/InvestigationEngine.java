package ar.edu.ucp.soc.orchestrator.service;

import ar.edu.ucp.soc.orchestrator.client.OllamaClient;
import ar.edu.ucp.soc.orchestrator.config.SystemPromptLoader;
import ar.edu.ucp.soc.orchestrator.model.AlertEvent;
import ar.edu.ucp.soc.orchestrator.model.Investigacion;
import ar.edu.ucp.soc.orchestrator.model.VeredictoIA;
import ar.edu.ucp.soc.orchestrator.repository.InvestigacionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvestigationEngine {

    private final OllamaClient ollamaClient;
    private final SystemPromptLoader systemPromptLoader;
    private final InvestigacionRepository investigacionRepository;
    private final ObjectMapper objectMapper;

    private static final int MAX_ITERACIONES = 5;

    public Investigacion investigar(Investigacion investigacion, AlertEvent alerta) {

        log.info("Iniciando investigación | id={} | agente={} | regla={}",
                investigacion.getId(), alerta.getAgentName(), alerta.getRuleName());

        // Cambiar estado a EN_INVESTIGACION
        investigacion.setEstado(Investigacion.Estado.EN_INVESTIGACION);
        investigacionRepository.save(investigacion);

        long inicio = System.currentTimeMillis();
        StringBuilder historial = new StringBuilder();
        String mensajeActual = construirMensajeInicial(alerta);
        int iteracion = 0;

        try {
            while (iteracion < MAX_ITERACIONES) {
                iteracion++;
                log.info("Iteración {} de {} | id={}", iteracion, MAX_ITERACIONES, investigacion.getId());

                // Llamar al LLM
                String respuestaLLM = ollamaClient.chat(
                        systemPromptLoader.getSystemPrompt(),
                        mensajeActual
                );

                historial.append("\n--- Iteración ").append(iteracion).append(" ---\n");
                historial.append("USER: ").append(mensajeActual).append("\n");
                historial.append("LLM: ").append(respuestaLLM).append("\n");

                log.debug("Respuesta LLM iteración {}: {}", iteracion, respuestaLLM);

                // Parsear la respuesta
                String accion = extraerAccion(respuestaLLM);

                if ("VEREDICTO_FINAL".equals(accion)) {
                    // El LLM emitió su veredicto
                    VeredictoIA veredicto = parsearVeredicto(respuestaLLM);
                    return cerrarInvestigacion(investigacion, veredicto, historial.toString(), inicio, iteracion);

                } else if ("USAR_HERRAMIENTA".equals(accion)) {
                    // El LLM necesita más información
                    String resultadoHerramienta = ejecutarHerramienta(respuestaLLM);
                    mensajeActual = "Resultado de la herramienta: " + resultadoHerramienta +
                            "\n\nContinuá el análisis con esta información adicional.";

                } else {
                    // Respuesta inesperada, intentar parsear como veredicto
                    log.warn("Formato inesperado en iteración {}, intentando parsear como veredicto", iteracion);
                    mensajeActual = "Tu respuesta anterior no siguió el formato JSON requerido. " +
                            "Respondé con {\"accion\": \"VEREDICTO_FINAL\", ...} o {\"accion\": \"USAR_HERRAMIENTA\", ...}";
                }
            }

            // Se agotaron las iteraciones sin veredicto
            log.warn("Se agotaron las iteraciones | id={}", investigacion.getId());
            VeredictoIA veredictoDefault = VeredictoIA.builder()
                    .clasificacion(VeredictoIA.Clasificacion.NECESITA_REVISION_HUMANA)
                    .confianza(0)
                    .explicacion("Se agotaron las iteraciones máximas sin emitir veredicto. Revisión humana requerida.")
                    .requiereRevisionHumana(true)
                    .build();

            return cerrarInvestigacion(investigacion, veredictoDefault, historial.toString(), inicio, iteracion);

        } catch (Exception e) {
            log.error("Error durante la investigación | id={}", investigacion.getId(), e);
            investigacion.setEstado(Investigacion.Estado.ERROR);
            investigacionRepository.save(investigacion);
            return investigacion;
        }
    }

    private String construirMensajeInicial(AlertEvent alerta) {
        return String.format("""
                Analizá la siguiente alerta de seguridad:
                
                - Agente: %s
                - Regla ID: %s
                - Regla: %s
                - Nivel de severidad: %d/15
                - Descripción: %s
                - IP fuente: %s
                - Timestamp: %s
                
                Determiná si es un incidente real o falso positivo.
                Usá herramientas si necesitás más contexto.
                """,
                alerta.getAgentName(),
                alerta.getRuleId(),
                alerta.getRuleName(),
                alerta.getRuleLevel() != null ? alerta.getRuleLevel() : 0,
                alerta.getDescription(),
                alerta.getSourceIp(),
                alerta.getTimestamp()
        );
    }

    private String extraerAccion(String respuestaLLM) {
        try {
            // Limpiar posibles markdown code blocks
            String json = respuestaLLM.trim()
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();
            var nodo = objectMapper.readTree(json);
            return nodo.get("accion") != null ? nodo.get("accion").asText() : "DESCONOCIDO";
        } catch (Exception e) {
            return "DESCONOCIDO";
        }
    }

    private String ejecutarHerramienta(String respuestaLLM) {
        // Por ahora devuelve un placeholder
        // En el próximo paso conectamos las herramientas reales (VT, AbuseIPDB, Wazuh)
        try {
            String json = respuestaLLM.trim()
                    .replace("```json", "").replace("```", "").trim();
            var nodo = objectMapper.readTree(json);
            String herramienta = nodo.get("herramienta") != null ?
                    nodo.get("herramienta").asText() : "desconocida";

            log.info("Herramienta solicitada: {} (placeholder por ahora)", herramienta);
            return "Herramienta " + herramienta + " ejecutada. Sin datos adicionales disponibles por el momento.";
        } catch (Exception e) {
            return "Error ejecutando herramienta.";
        }
    }

    private VeredictoIA parsearVeredicto(String respuestaLLM) {
        try {
            String json = respuestaLLM.trim()
                    .replace("```json", "").replace("```", "").trim();
            var nodo = objectMapper.readTree(json);

            return VeredictoIA.builder()
                    .clasificacion(VeredictoIA.Clasificacion.valueOf(
                            nodo.get("clasificacion") != null ?
                                    nodo.get("clasificacion").asText() : "NECESITA_REVISION_HUMANA"))
                    .confianza(nodo.get("confianza") != null ? nodo.get("confianza").asInt() : 50)
                    .explicacion(nodo.get("explicacion") != null ? nodo.get("explicacion").asText() : "")
                    .accionRecomendada(nodo.get("accion_recomendada") != null ?
                            nodo.get("accion_recomendada").asText() : "")
                    .requiereRevisionHumana(false)
                    .build();
        } catch (Exception e) {
            log.error("Error parseando veredicto", e);
            return VeredictoIA.builder()
                    .clasificacion(VeredictoIA.Clasificacion.NECESITA_REVISION_HUMANA)
                    .confianza(0)
                    .explicacion("Error al parsear el veredicto del LLM.")
                    .requiereRevisionHumana(true)
                    .build();
        }
    }

    private Investigacion cerrarInvestigacion(Investigacion inv, VeredictoIA veredicto,
                                              String historial, long inicio, int iteraciones) {
        try {
            inv.setVeredictoJson(objectMapper.writeValueAsString(veredicto));
        } catch (Exception e) {
            inv.setVeredictoJson("{}");
        }

        inv.setHistorialMensajes(historial);
        inv.setIteraciones(iteraciones);
        inv.setDuracionMs(System.currentTimeMillis() - inicio);
        inv.setTimestampVeredicto(LocalDateTime.now());

        // Determinar estado final según clasificación
        if (veredicto.getClasificacion() == VeredictoIA.Clasificacion.FALSO_POSITIVO) {
            inv.setEstado(Investigacion.Estado.CERRADA_FALSO_POSITIVO);
        } else if (veredicto.getClasificacion() == VeredictoIA.Clasificacion.NECESITA_REVISION_HUMANA) {
            inv.setEstado(Investigacion.Estado.PENDIENTE_REVISION_HUMANA);
        } else {
            inv.setEstado(Investigacion.Estado.VEREDICTO_EMITIDO);
        }

        inv.setSoarDetonado(false);
        investigacionRepository.save(inv);

        log.info("Investigación completada | id={} | clasificacion={} | confianza={} | duracion={}ms",
                inv.getId(),
                veredicto.getClasificacion(),
                veredicto.getConfianza(),
                inv.getDuracionMs());

        return inv;
    }
}