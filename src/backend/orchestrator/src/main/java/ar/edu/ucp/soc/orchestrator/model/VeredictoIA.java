package ar.edu.ucp.soc.orchestrator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VeredictoIA {

    private Clasificacion clasificacion;
    private Integer confianza; // 0-100
    private String explicacion;
    private String accionRecomendada;
    private List<String> ttps; // Técnicas MITRE ATT&CK identificadas
    private List<String> evidenciasUsadas; // CTI consultadas
    private Boolean requiereRevisionHumana;

    public enum Clasificacion {
        CRITICO,
        FALSO_POSITIVO,
        SOSPECHOSO,
        NECESITA_REVISION_HUMANA
    }

    // Devuelve true si el veredicto requiere detonar el SOAR
    public boolean requiereAccionAutomatica() {
        return clasificacion == Clasificacion.CRITICO
                && confianza != null
                && confianza >= 70
                && Boolean.FALSE.equals(requiereRevisionHumana);
    }
}