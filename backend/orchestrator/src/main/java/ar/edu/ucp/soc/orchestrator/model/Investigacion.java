package ar.edu.ucp.soc.orchestrator.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "investigaciones")
public class Investigacion {

    @Id
    private String id; // UUID generado al crear la investigación

    private String alertaId;
    private String agenteName;
    private String ruleName;
    private Integer ruleLevel;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    private Integer iteraciones;

    @Column(columnDefinition = "TEXT")
    private String veredictoJson; // Veredicto serializado como JSON

    @Column(columnDefinition = "TEXT")
    private String historialMensajes; // Historial de conversación con el LLM

    private LocalDateTime timestampIngesta;
    private LocalDateTime timestampVeredicto;
    private Long duracionMs; // Tiempo total de investigación en milisegundos

    private Boolean soarDetonado;
    private Boolean aprobadaPorHumano;
    private String notaHumano;

    public enum Estado {
        RECIBIDA,
        EN_INVESTIGACION,
        VEREDICTO_EMITIDO,
        SOAR_DETONADO,
        CERRADA_FALSO_POSITIVO,
        PENDIENTE_REVISION_HUMANA,
        RECHAZADA_POR_HUMANO,
        ERROR
    }

    // Calcula el tiempo de investigación en segundos
    public long getDuracionSegundos() {
        if (duracionMs == null) return 0;
        return duracionMs / 1000;
    }

    // Indica si la investigación está en un estado final
    public boolean esFinal() {
        return estado == Estado.SOAR_DETONADO
                || estado == Estado.CERRADA_FALSO_POSITIVO
                || estado == Estado.RECHAZADA_POR_HUMANO
                || estado == Estado.ERROR;
    }
}
