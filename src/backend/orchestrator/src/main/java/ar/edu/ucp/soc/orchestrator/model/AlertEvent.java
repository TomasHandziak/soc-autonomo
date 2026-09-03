package ar.edu.ucp.soc.orchestrator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertEvent {

    private String id;
    private String agentId;
    private String agentName;
    private String ruleId;
    private String ruleName;
    private Integer ruleLevel;
    private String description;
    private String sourceIp;
    private String filePath;
    private String rawLog;
    private LocalDateTime timestamp;
    private SeverityLevel severity;

    public enum SeverityLevel {
        INFO,       // nivel 1-4
        LOW,        // nivel 5-6
        MEDIUM,     // nivel 7-9
        HIGH,       // nivel 10-12
        CRITICAL    // nivel 13-15
    }

    // Convierte el nivel numérico de Wazuh (1-15) a SeverityLevel
    public static SeverityLevel fromWazuhLevel(int level) {
        if (level <= 4)  return SeverityLevel.INFO;
        if (level <= 6)  return SeverityLevel.LOW;
        if (level <= 9)  return SeverityLevel.MEDIUM;
        if (level <= 12) return SeverityLevel.HIGH;
        return SeverityLevel.CRITICAL;
    }
}