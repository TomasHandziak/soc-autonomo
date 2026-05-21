package ar.edu.ucp.soc.orchestrator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FunctionCall {

    private String nombre;
    private Map<String, Object> parametros;
    private String resultado;
    private Long duracionMs;
    private Boolean exitosa;

    public enum NombreFuncion {
        BUSCAR_LOGS_HISTORICOS,
        CONSULTAR_VIRUSTOTAL,
        CONSULTAR_ABUSEIPDB,
        BUSCAR_TACTICA_MITRE,
        BUSCAR_ALERTAS_PREVIAS
    }
}