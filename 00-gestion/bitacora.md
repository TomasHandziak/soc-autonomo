# Bitácora - CyberShield

**Proyecto Integrador Final:** CyberShield - Sistema de Triage Autónomo de Alertas de Seguridad basado en Agentes de Inteligencia Artificial
**Coautores:** Tomás Handziak y Manuel Zielinski
**Repositorio:** https://github.com/TomasHandziak/soc-autonomo (rama develop) - privado, con el docente tutor agregado como colaborador.

> Bitácora conjunta, conforme a confirmación del docente tutor PosDr. Darío Ezequiel Díaz (mail, 01/09/2026): *"un único documento de bitácora entre Manuel y vos, ya que ustedes están tomando las decisiones del proyecto"*. Cada autor copia igualmente el contenido acumulado en su propia tarea individual «AE1 · Bitácora Individual de Proceso y Decisiones» del aula virtual.

> **Nota de trazabilidad:** el desarrollo backend del proyecto se realizó en gran parte desde una computadora de la facultad utilizando la cuenta de GitHub de Tomás Handziak, por lo que los commits referenciados en las entradas de abril a junio figuran bajo su usuario aun cuando la decisión haya sido conjunta. La autoría real de cada aporte queda declarada en el elemento 4 de cada entrada, conforme a lo pactado en el Acta de Constitución (Instrumento 2).

> **Criterio de resolución de desacuerdos** (Acta de Constitución, Instrumento 2, punto 2.3): comparación estructurada de todos los puntos en discusión, apoyada en diagramas de Venn u otra matriz de decisión por criterios ponderados, y validación conjunta antes de avanzar. Ante empate real persistente, decide en última instancia Manuel Zielinski.

---

### 01/09/2026 — Guía de entrevista al referente y método de validación del prototipo v0

**1 · Decisión.** Se adopta la guía final de preguntas para la entrevista con el referente del MSSP (segunda fuente primaria exigida por el Dictamen Técnico N.º 02/2026), y se define el método de validación del prototipo v0: que el referente lo evalúe dando su opinión en la misma reunión de la entrevista, en lugar de una validación exclusivamente por laboratorio.

**2 · Alternativas y criterio de descarte.** Sobre la validación se evaluó (a) validación en laboratorio con el corpus público de prueba (CICIDS2018 y simulaciones de Atomic Red Team) sobre infraestructura de la facultad, descartada porque el docente tutor confirmó por escrito que correspondía la validación por opinión del referente; (b) constancia de validación firmada por una persona ajena al proyecto, con su opinión registrada, adoptada. Sobre las preguntas, se evaluó un bloque de doce preguntas en seis ejes devuelto por el tutor, que incluía pedidos de exportación de datos del sistema de tickets y de clasificación ciega de alertas por otro analista; se descartaron esos dos pedidos puntuales porque exceden lo exigible a un entrevistado que además es superior jerárquico de uno de los autores en su trabajo real.

**3 · Evidencia.** Mail del docente tutor PosDr. Darío Ezequiel Díaz (01/09/2026) confirmando el método de validación del prototipo, y aclaración del ayudante José Luis Medina sobre los límites de lo que puede preguntarse al referente (no está obligado a responder todas las preguntas).

**4 · Aporte personal.** Tomás gestionó el intercambio de mails con el docente tutor y coordinó la fecha de la entrevista con su líder en el MSSP. Manuel revisó la guía de preguntas devuelta por el tutor y ajustó la versión final de diez preguntas. Artefacto: sin commit asociado (trabajo de coordinación y redacción, guía de entrevista a subir en 01-relevamiento/).

**5 · Desacuerdo.** No hubo desacuerdo entre los coautores en este punto: el ajuste de la guía respondió a la devolución del docente tutor y a los límites propios de la relación laboral con el referente, sin necesidad de aplicar la matriz de decisión del Acta de Constitución.

**6 · Herramienta auxiliar.** Ninguna. La guía de entrevista se redactó y ajustó directamente por el equipo.

---

### 31/08/2026 — Prototipo v0 navegable y tablero de gestión

**1 · Decisión.** Se construye el prototipo v0 como maqueta navegable en Figma, con cinco pantallas conectadas (bandeja de alertas, detalle de alerta, investigación en curso, veredicto, historial de veredictos), y se organiza el tablero de gestión de la AE1 en Trello, con trece tarjetas cubriendo las tres semanas de relevamiento.

**2 · Alternativas y criterio de descarte.** Se evaluó GitHub Projects, armado primero con los mismos campos de responsable, estado, semana y fecha, frente a Trello; se descartó mantener los dos tableros en paralelo y se migró a Trello, porque el equipo ya contaba con una cuenta activa en esa herramienta y la consigna admite cualquier herramienta de tablero, y duplicar la carga exponía el riesgo de que se audite un tablero desactualizado.

**3 · Evidencia.** Prototipo v0: https://www.figma.com/design/rSXB1Qu8TBw6iBMbZ7ku0r. Tablero: https://trello.com/b/iqiXmRAP/cybershield-ae1-relevamiento, con las trece tarjetas cargadas y clasificadas por responsable, estado y fecha.

**4 · Aporte personal.** Tomás y Manuel definieron juntos el contenido de las cinco pantallas del prototipo (a partir del recorrido real de una alerta relevado en el Capítulo I) y la organización de las tarjetas del tablero por semana y responsable, distribuyendo entre los dos las trece tareas según el rol de cada uno. Artefacto: enlaces de Figma y Trello citados arriba (sin commit asociado, prototipo fuera del repositorio de código).

**5 · Desacuerdo.** Ninguno registrado en esta entrada.

**6 · Herramienta auxiliar.** Figma y Trello, para el maquetado de las cinco pantallas del prototipo y la carga de las trece tarjetas del tablero.

---

### 17/06/2026 — Orquestación de Threat Intelligence (CTI)

**1 · Decisión.** Se adopta invocación dinámica por función (function calling) para el consumo de las fuentes externas de inteligencia de amenazas (VirusTotal, AbuseIPDB, MITRE ATT&CK): el propio LLM decide qué fuente consultar según el contexto de cada alerta, en lugar de un consumo secuencial estático.

**2 · Alternativas y criterio de descarte.** Se evaluó (a) consumo secuencial estático, consultando por defecto todas las APIs de CTI ante cualquier IP o hash sospechoso, descartado porque agotaba rápidamente las cuotas de las cuentas gratuitas y agregaba latencia innecesaria incluso ante falsos positivos evidentes; (b) consumo dinámico por función, adoptado. Criterio: preservar cuota de API y reducir la latencia total del sistema.

**3 · Evidencia.** FunctionDispatcher implementado, permitiendo al LLM invocar a demanda VirusTotalClient, AbuseIPDBClient o MitreAttackService solo cuando el análisis heurístico lo requiere. En la misma sesión se diagnosticó y corrigió un error de autenticación (HTTP 401) en WazuhIndexerClient.

**4 · Aporte personal.** Tomás implementó y probó los tres clientes contra sus APIs reales con Postman (commit 3050ce0, `src/backend/orchestrator/src/main/java/ar/edu/ucp/soc/orchestrator/client/VirusTotalClient.java` y AbuseIPDBClient.java), y diagnosticó el error 401 del WazuhIndexerClient (commit ca004b2, `src/backend/orchestrator/src/main/java/ar/edu/ucp/soc/orchestrator/client/WazuhIndexerClient.java`). Manuel identificó el riesgo de agotamiento de cuota del enfoque estático y diseñó el `FunctionDispatcher` (`src/backend/orchestrator/src/main/java/ar/edu/ucp/soc/orchestrator/service/FunctionDispatcher.java`, commits 3050ce0 y ca004b2).

**5 · Desacuerdo.** Tomás propuso un flujo lineal: ante cada alerta, consultar por defecto VirusTotal y AbuseIPDB y recién después pasarle todo a la IA. Manuel planteó que, con cuentas gratuitas, ese flujo iba a chocar contra los rate limits ante un pico de alertas. Se aplicó la matriz de decisión pactada en el Acta de Constitución, comparando ambos enfoques por cuota consumida y latencia; no hizo falta el desempate de Manuel porque la comparación fue unánime a favor del FunctionDispatcher.

**6 · Herramienta auxiliar.** Postman, para probar los endpoints de VirusTotal, AbuseIPDB y MITRE ATT&CK antes de programar los clientes en Java.

---

### 21/05/2026 — Motor de IA y aislamiento de datos (OPSEC)

**1 · Decisión.** Se adopta un LLM local vía Ollama (qwen2.5:14b) para el análisis de alertas, en lugar de una API cloud.

**2 · Alternativas y criterio de descarte.** Se evaluó (a) API cloud (OpenAI/Anthropic), con integración más rápida y mayor capacidad de razonamiento sin infraestructura propia, descartada; (b) LLM local aislado de internet, ejecutado en hardware propio, adoptada. Criterio: con Sysmon ya capturando telemetría real de los endpoints (433 eventos en la primera prueba), enviar esos logs a servidores comerciales de terceros representaba un riesgo de fuga de datos inaceptable para un entorno de seguridad real.

**3 · Evidencia.** InvestigationEngine funcionando de punta a punta (ingesta de alerta, análisis, persistencia) con los datos procesados íntegramente dentro de la infraestructura propia, sin llamadas a servicios externos de IA. Principio de ingeniería invocado: minimización de superficie de exposición de datos (confidencialidad).

**4 · Aporte personal.** Manuel modeló las entidades de dominio AlertEvent, VeredictoIA, Investigacion y FunctionCall (commit fe64e9e, `src/backend/orchestrator/src/main/java/ar/edu/ucp/soc/orchestrator/model/`) y construyó el pipeline AlertService + InvestigacionRepository (commit 751282d). Tomás instaló y configuró Sysmon y el Wazuh Agent, obteniendo la captura de los 433 eventos (commit aedea12, `.env.example`). Manuel realizó la integración de OllamaClient (commit b74c382, `src/backend/orchestrator/src/main/java/ar/edu/ucp/soc/orchestrator/client/OllamaClient.java`) y, tras identificar el riesgo de OPSEC, el InvestigationEngine y el ajuste del SystemPrompt (commit b96b2aa, `src/backend/orchestrator/src/main/java/ar/edu/ucp/soc/orchestrator/service/InvestigationEngine.java` y `src/backend/orchestrator/src/main/resources/system_prompt.txt`).

**5 · Desacuerdo.** Tomás sugirió conectar el backend a la API de OpenAI para ganar potencia de razonamiento sin castigar el hardware local. Manuel sostuvo la posición opuesta por el riesgo de OPSEC descripto en el punto 2. Se aplicó la matriz de decisión del Acta de Constitución sobre riesgo de fuga de datos versus capacidad de razonamiento, y se acordó integrar OllamaClient sin necesidad de recurrir al desempate.

**6 · Herramienta auxiliar.** Ninguna. La integración de Ollama y el ajuste del SystemPrompt se realizaron directamente sobre el código.

---

### 07/05/2026 — Arquitectura y framework del backend

**1 · Decisión.** Se adopta Java con el framework Spring Boot como tecnología principal para orquestar la lógica del motor de triage.

**2 · Alternativas y criterio de descarte.** Se evaluó (a) Python con FastAPI/Flask, estándar de la industria para scripts de seguridad y con conexión más directa a modelos de IA, descartada; (b) Java con Spring Boot, adoptada. Criterio: el proyecto no se limitaba a un script de análisis, sino que requería modelar entidades de dominio propias (AlertEvent, VeredictoIA, Investigacion) con una capa de persistencia estructurada y mantenible a largo plazo.

**3 · Evidencia.** Proyecto base estructurado en paquetes, con las dependencias (Web, JPA, H2, Ollama, Actuator) configuradas y funcionando, y los perfiles de entorno (dev) operativos desde esta misma sesión.

**4 · Aporte personal.** La decisión de la arquitectura del backend - adopción de Spring Boot y diseño de capas— fue realizada íntegramente por Manuel. Tomás generó el esqueleto inicial del proyecto con Spring Initializr y lo ejecutó sobre su máquina (commits 6bc2511, a06dbe4 y bae0bde, `src/backend/orchestrator/pom.xml` y `src/backend/orchestrator/src/main/resources/application.yaml`).

**5 · Desacuerdo.** Tomás impulsaba Python por estar el ecosistema de IA construido mayormente sobre ese lenguaje. Manuel sostuvo Java con Spring Boot, con el argumento de que el corazón del sistema era el pipeline de datos y no la IA en sí. Se aplicó la matriz de decisión del Acta de Constitución, comparando ambas opciones por mantenibilidad y ajuste al dominio del problema; no hizo falta el desempate de Manuel porque, al mostrar cómo Spring Data JPA resolvía de forma tipada la interacción con H2, Tomás acordó con el criterio de Manuel.

**6 · Herramienta auxiliar.** Spring Initializr, para generar el esqueleto del proyecto con las dependencias exactas (Web, JPA, H2, Ollama, Actuator).

---

### 30/04/2026 — Despliegue de infraestructura central (Wazuh)

**1 · Decisión.** Se adopta Docker Compose, sobre el kit oficial wazuh-docker (v4.7.3), para el despliegue del stack de Wazuh, reemplazando la instalación nativa con la que se hicieron las pruebas iniciales.

**2 · Alternativas y criterio de descarte.** Se evaluó (a) instalación nativa sobre el sistema operativo, con la que se hicieron las primeras pruebas del entorno de monitorización, descartada; (b) Docker Compose con el kit oficial wazuh-docker, adoptada. Criterio: mantener el stack (indexer, manager, dashboard) ordenado y reproducible, algo que se volvió determinante apenas se empezó a sumar la integración con la capa de IA sobre el mismo entorno.

**3 · Evidencia.** Stack de Wazuh operativo mediante docker-compose.wazuh.yml y el kit wazuh-docker-4.7.3, con los certificados SSL entre indexer, manager y dashboard generados y validados correctamente dentro de ese entorno en contenedores.

**4 · Aporte personal.** Tomás propuso migrar de la instalación nativa a Docker Compose, y ejecutó esa migración y la configuración de los tres componentes de Wazuh sobre la máquina de la facultad, bajo su cuenta de GitHub (commits 68a291f, 9934051 y 78f0ab1, `src/wazuh-docker-4.7.3/` y `src/docker-compose.wazuh.yml`). Manuel validó que el cambio no comprometiera la estabilidad de los logs y acompañó la verificación de los certificados generados.

**5 · Desacuerdo.** No hubo desacuerdo entre los coautores: Tomás propuso el cambio de instalación nativa a Docker Compose una vez comprobado, en las pruebas iniciales, que la contenerización iba a facilitar ordenar el stack a medida que se sumaran más componentes (Wazuh y, más adelante, la integración con IA), y Manuel coincidió con el criterio sin necesidad de aplicar la matriz de decisión del Acta de Constitución.

**6 · Herramienta auxiliar.** Docker Compose y el kit oficial wazuh-docker (repositorio de Wazuh) como base de la configuración del stack en contenedores.

---

**Nota de cierre.** El trabajo de desarrollo backend posterior al 17 de junio (Sprint 3-4: US-018, US-042, corrección de codificación UTF-8, regeneración de credencial de VirusTotal expuesta) todavía no está pusheado al repositorio remoto al momento de esta entrega, y por lo tanto no tiene entrada propia en esta bitácora. Se incorporará arriba de todo, con su fecha correspondiente, una vez subido.