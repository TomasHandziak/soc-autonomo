# Analista de SOC Autónomo Multi-Agente

Sistema de Inteligencia Artificial Agéntica para automatización del triaje de alertas en Centros de Operaciones de Seguridad (SOC).

## Autores
- Tomás Handziak — Ciberseguridad / IA
- Manuel Zielinski — Backend
- Todos los commits fueron realizados en conjunto durante el cursado en la Universidad bajo el mismo usuario de TomasHandziak, dentro de esta cuenta de github.

**Universidad de la Cuenca del Plata — Ingeniería en Sistemas — Tesis Final 2026**

---

## ¿Qué hace este sistema?

Cuando el SIEM (Wazuh) detecta una alerta de seguridad, en lugar de que un analista humano la investigue manualmente, el sistema:

1. Recibe la alerta automáticamente
2. La envía a un modelo de IA (LLM local) para análisis
3. La IA investiga de forma autónoma consultando logs históricos, VirusTotal y AbuseIPDB
4. Emite un veredicto (Incidente Real / Falso Positivo)
5. Si es crítico, detona una respuesta automatizada (SOAR)

---

## Requisitos

- Windows 11
- Docker Desktop 4.x+ con WSL2
- Ollama para Windows (ollama.com)
- Java 21 (OpenJDK)
- IntelliJ IDEA
- 16GB+ RAM (64GB recomendado)
- GPU NVIDIA con CUDA (RTX 5070 recomendado)

---

## Estructura del proyecto

soc-autonomo/
├── backend/     → Backend Spring Boot (Java 21)
├── wazuh/       → Configuraciones del SIEM
├── ollama/      → Modelfile del LLM personalizado
├── soar/        → Workflows de n8n
├── docs/        → Arquitectura, ADRs, documentación
└── tests/       → Scripts de evaluación y benchmarks

---

## Inicio rápido (entorno de desarrollo)

### 1. Clonar el repositorio
```bash
git clone https://github.com/TomasHandziak/soc-autonomo.git
cd soc-autonomo
```

### 2. Configurar variables de entorno
Copiar `.env.example` a `.env` y completar los valores reales.

### 3. Levantar las dependencias (Wazuh + n8n)
```bash
docker compose -f docker-compose.dev.yml up -d
```

### 4. Correr el backend
Abrir `/backend` en IntelliJ IDEA y ejecutar con perfil `dev`.

---

## Stack tecnológico

| Componente | Tecnología |
|---|---|
| Backend | Java 21 + Spring Boot 3.x |
| SIEM | Wazuh 4.x (Docker) |
| LLM | Ollama + DeepSeek / Qwen 14B |
| CTI | VirusTotal + AbuseIPDB + MITRE ATT&CK |
| SOAR | n8n (Docker) |
| Infraestructura | Docker Desktop + WSL2 |

---

*Documentación completa en /docs/*
