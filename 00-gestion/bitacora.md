# Bitacora - CyberShield

**Proyecto Integrador Final:** CyberShield - Sistema de Triage Autonomo de Alertas de Seguridad basado en Agentes de Inteligencia Artificial
**Coautores:** Tomas Handziak y Manuel Zielinski
**Repositorio:** https://github.com/TomasHandziak/soc-autonomo (rama develop) - privado, con el docente tutor agregado como colaborador.

> Bitacora conjunta, conforme a confirmacion del docente tutor PosDr. Dario Ezequiel Diaz (mail, 01/09/2026): "un unico documento de bitacora entre Manuel y vos, ya que ustedes estan tomando las decisiones del proyecto". Cada autor copia igualmente el contenido acumulado en su propia tarea individual AE1 - Bitacora Individual de Proceso y Decisiones del aula virtual.

> **Nota de trazabilidad:** el desarrollo backend del proyecto se realizo en gran parte desde una computadora de la facultad utilizando la cuenta de GitHub de Tomas Handziak, por lo que los commits referenciados en las entradas de abril a junio figuran bajo su usuario aun cuando la decision haya sido conjunta. La autoria real de cada aporte queda declarada en el elemento 4 de cada entrada, conforme a lo pactado en el Acta de Constitucion (Instrumento 2).

> **Criterio de resolucion de desacuerdos** (Acta de Constitucion, Instrumento 2, punto 2.3): comparacion estructurada de todos los puntos en discusion, apoyada en diagramas de Venn u otra matriz de decision por criterios ponderados, y validacion conjunta antes de avanzar. Ante empate real persistente, decide en ultima instancia Manuel Zielinski.

### 04/09/2026 -- Entrevista con el referente y validacion del prototipo v0

**1 - Decision.** Se ejecuta la entrevista de vision de negocio con Gabriel Latorre (referente del MSSP, segunda fuente primaria exigida por el Dictamen Tecnico N.o 02/2026) y se valida el prototipo v0 en la misma reunion, conforme al metodo acordado con el docente tutor y el ayudante Jose Luis Medina.

**2 - Alternativas y criterio de descarte.** No aplica -- esta entrada ejecuta lo ya decidido en la entrada del 01/09/2026 (guia de diez preguntas y validacion por opinion del referente).

**3 - Evidencia.** Reunion por Google Meet, viernes 04/09/2026, con transcripcion automatica habilitada y consentimiento explicito grabado al inicio (das consentimiento para que transcriba esta entrevista y la use como fuente en mi trabajo? -- Si, si, totalmente, tenes mi consentimiento, dale nomas.). Invitacion de calendario aceptada por Gabriel Latorre (glatorreteco@gmail.com), evento Entrevista de Validacion del problema y prototipoV0. Coordinacion previa documentada por chat, explicitando el requisito de fuente primaria y la garantia de anonimato de la organizacion y del entrevistado. Transcripcion completa conservada como evidencia (Portafolio Digital, 01-relevamiento).

Hallazgos relevantes para el Capitulo I y el Capitulo II:
- Triage manual: entre 5 y 20 minutos por alerta segun complejidad; casos cerrados en aproximadamente 24 horas si el cliente responde con rapidez. Confirma, con matiz de rango superior, la linea de base ya relevada por autoobservacion (5-15 minutos).
- SLA con los 13 clientes: se cumplen, pero atados a la cantidad de reglas y fuentes que cada cliente ingesta al SIEM, no al volumen bruto de alertas; el margen de cumplimiento se sostiene exigiendo al limite la capacidad de los turnos.
- Sin reclamos formales ni contratos en riesgo documentados hasta la fecha, aunque el margen de error se reduce con cada cliente nuevo que se suma sin cambios operativos.
- Historia del problema: escalo progresivamente desde un analista y un cliente hasta la dotacion y cartera actual (13 clientes), a medida que crecio la operacion.
- Ya se evaluo ampliar dotacion de personal como respuesta, pero se reconoce que escalar linealmente por cada cliente nuevo no es sustentable, ni por costo ni por operacion.
- Solucion ya probada: ajuste de umbrales estaticos en las reglas del SIEM; limite reconocido: hay comportamientos humanos que no se pueden filtrar automaticamente con una regla fija.
- Dato nuevo no relevado antes: la organizacion esta en transicion activa hacia un SOAR con playbooks propios, lo que abre una via de integracion directa para el veredicto del sistema propuesto (disparo automatico del playbook correspondiente).
- Validacion explicita de la propuesta: todo lo que nos ayude a automatizar el triage... es muy bienvenido, con expectativa adicional de integracion con el SOAR en desarrollo.
- Unica reserva expresada: cuidar que la automatizacion no genere falsos negativos, y validar bien la interoperabilidad con el SOAR y los playbooks.

Validacion del prototipo v0: se compartio pantalla y se recorrio la maqueta de Figma. El referente comprendio el flujo (SIEM -> agente -> analista o disparo de playbook en el SOAR), pregunto puntualmente por el registro de auditoria de las alertas descartadas (confirmado: el razonamiento del agente queda logueado para revision posterior) y por el criterio de mapeo hacia el playbook correspondiente. Devolucion final registrada textualmente: Para mi la logica cierra bien [...] Por mi parte esta aprobado.

**4 - Aporte personal.** Tomas condujo la entrevista, compartio pantalla para la validacion del prototipo y coordino la logistica de la reunion (invitacion de calendario, consentimiento grabado). [Participacion de Manuel en esta sesion: pendiente de confirmar -- no consta en la transcripcion disponible.]

**5 - Desacuerdo.** No aplica a esta entrada.

**6 - Herramienta auxiliar.** Grabacion y transcripcion automatica mediante Google Meet.

> **Nota de consistencia pendiente de resolver.** El referente describe una dotacion de ocho analistas en tres turnos (3 manana, 3 tarde, 1 noche) mas un especialista, cifra que difiere de la registrada previamente por autoobservacion (seis analistas, dos turnos). Se prioriza el dato del referente por tratarse de fuente primaria declarada, pero se deja pendiente la conciliacion explicita antes de la redaccion final del Capitulo I. El referente tambien se refirio a Tomas como lider del equipo en el mismo pasaje -- a confirmar si corresponde ajustar la descripcion del rol de Tomas en el Instrumento 1 y en el apartado I.1.

---

### 01/09/2026 -- Guia de entrevista al referente y metodo de validacion del prototipo v0

**1 - Decision.** Se adopta la guia final de preguntas para la entrevista con el referente del MSSP (segunda fuente primaria exigida por el Dictamen Tecnico N.o 02/2026), y se define el metodo de validacion del prototipo v0: que el referente lo evalue dando su opinion en la misma reunion de la entrevista, en lugar de una validacion exclusivamente por laboratorio.

**2 - Alternativas y criterio de descarte.** Sobre la validacion se evaluo (a) validacion en laboratorio con el corpus publico de prueba (CICIDS2018 y simulaciones de Atomic Red Team) sobre infraestructura de la facultad, descartada porque el docente tutor confirmo por escrito que correspondia la validacion por opinion del referente; (b) constancia de validacion firmada por una persona ajena al proyecto, con su opinion registrada, adoptada. Sobre las preguntas, se evaluo un bloque de doce preguntas en seis ejes devuelto por el tutor, que incluia pedidos de exportacion de datos del sistema de tickets y de clasificacion ciega de alertas por otro analista; se descartaron esos dos pedidos puntuales porque exceden lo exigible a un entrevistado que ademas es superior jerarquico de uno de los autores en su trabajo real.

**3 - Evidencia.** Mail del docente tutor PosDr. Dario Ezequiel Diaz (01/09/2026) confirmando el metodo de validacion del prototipo, y aclaracion del ayudante Jose Luis Medina sobre los limites de lo que puede preguntarse al referente (no esta obligado a responder todas las preguntas).

**4 - Aporte personal.** Tomas gestiono el intercambio de mails con el docente tutor y coordino la fecha de la entrevista con su lider en el MSSP. Manuel reviso la guia de preguntas devuelta por el tutor y ajusto la version final de diez preguntas. Artefacto: sin commit asociado (trabajo de coordinacion y redaccion, guia de entrevista a subir en 01-relevamiento/).

**5 - Desacuerdo.** No hubo desacuerdo entre los coautores en este punto: el ajuste de la guia respondio a la devolucion del docente tutor y a los limites propios de la relacion laboral con el referente, sin necesidad de aplicar la matriz de decision del Acta de Constitucion.

**6 - Herramienta auxiliar.** Ninguna. La guia de entrevista se redacto y ajusto directamente por el equipo.

---

### 31/08/2026 -- Prototipo v0 navegable y tablero de gestion

**1 - Decision.** Se construye el prototipo v0 como maqueta navegable en Figma, con cinco pantallas conectadas (bandeja de alertas, detalle de alerta, investigacion en curso, veredicto, historial de veredictos), y se organiza el tablero de gestion de la AE1 en Trello, con trece tarjetas cubriendo las tres semanas de relevamiento.

**2 - Alternativas y criterio de descarte.** Se evaluo GitHub Projects, armado primero con los mismos campos de responsable, estado, semana y fecha, frente a Trello; se descarto mantener los dos tableros en paralelo y se migro a Trello, porque el equipo ya contaba con una cuenta activa en esa herramienta y la consigna admite cualquier herramienta de tablero, y duplicar la carga exponia el riesgo de que se audite un tablero desactualizado.

**3 - Evidencia.** Prototipo v0: https://www.figma.com/design/rSXB1Qu8TBw6iBMbZ7ku0r. Tablero: https://trello.com/b/iqiXmRAP/cybershield-ae1-relevamiento, con las trece tarjetas cargadas y clasificadas por responsable, estado y fecha.

**4 - Aporte personal.** Tomas y Manuel definieron juntos el contenido de las cinco pantallas del prototipo (a partir del recorrido real de una alerta relevado en el Capitulo I) y la organizacion de las tarjetas del tablero por semana y responsable, distribuyendo entre los dos las trece tareas segun el rol de cada uno. Artefacto: enlaces de Figma y Trello citados arriba (sin commit asociado, prototipo fuera del repositorio de codigo).

**5 - Desacuerdo.** Ninguno registrado en esta entrada.

**6 - Herramienta auxiliar.** Figma y Trello, para el maquetado de las cinco pantallas del prototipo y la carga de las trece tarjetas del tablero.

---

### 17/06/2026 -- Orquestacion de Threat Intelligence (CTI)

**1 - Decision.** Se adopta invocacion dinamica por funcion (function calling) para el consumo de las fuentes externas de inteligencia de amenazas (VirusTotal, AbuseIPDB, MITRE ATT&CK): el propio LLM decide que fuente consultar segun el contexto de cada alerta, en lugar de un consumo secuencial estatico.

**2 - Alternativas y criterio de descarte.** Se evaluo (a) consumo secuencial estatico, consultando por defecto todas las APIs de CTI ante cualquier IP o hash sospechoso, descartado porque agotaba rapidamente las cuotas de las cuentas gratuitas y agregaba latencia innecesaria incluso ante falsos positivos evidentes; (b) consumo dinamico por funcion, adoptado. Criterio: preservar cuota de API y reducir la latencia total del sistema.

**3 - Evidencia.** FunctionDispatcher implementado, permitiendo al LLM invocar a demanda VirusTotalClient, AbuseIPDBClient o MitreAttackService solo cuando el analisis heuristico lo requiere. En la misma sesion se diagnostico y corrigio un error de autenticacion (HTTP 401) en WazuhIndexerClient.

**4 - Aporte personal.** Tomas implemento y probo los tres clientes contra sus APIs reales con Postman (commit 3050ce0, `src/backend/orchestrator/src/main/java/ar/edu/ucp/soc/orchestrator/client/VirusTotalClient.java` y AbuseIPDBClient.java), y diagnostico el error 401 del WazuhIndexerClient (commit ca004b2, `src/backend/orchestrator/src/main/java/ar/edu/ucp/soc/orchestrator/client/WazuhIndexerClient.java`). Manuel identifico el riesgo de agotamiento de cuota del enfoque estatico y diseno el FunctionDispatcher (`src/backend/orchestrator/src/main/java/ar/edu/ucp/soc/orchestrator/service/FunctionDispatcher.java`, commits 3050ce0 y ca004b2).

**5 - Desacuerdo.** Tomas propuso un flujo lineal: ante cada alerta, consultar por defecto VirusTotal y AbuseIPDB y recien despues pasarle todo a la IA. Manuel planteo que, con cuentas gratuitas, ese flujo iba a chocar contra los rate limits ante un pico de alertas. Se aplico la matriz de decision pactada en el Acta de Constitucion, comparando ambos enfoques por cuota consumida y latencia; no hizo falta el desempate de Manuel porque la comparacion fue unanime a favor del FunctionDispatcher.

**6 - Herramienta auxiliar.** Postman, para probar los endpoints de VirusTotal, AbuseIPDB y MITRE ATT&CK antes de programar los clientes en Java.

---

### 21/05/2026 -- Motor de IA y aislamiento de datos (OPSEC)

**1 - Decision.** Se adopta un LLM local via Ollama (qwen2.5:14b) para el analisis de alertas, en lugar de una API cloud.

**2 - Alternativas y criterio de descarte.** Se evaluo (a) API cloud (OpenAI/Anthropic), con integracion mas rapida y mayor capacidad de razonamiento sin infraestructura propia, descartada; (b) LLM local aislado de internet, ejecutado en hardware propio, adoptada. Criterio: con Sysmon ya capturando telemetria real de los endpoints (433 eventos en la primera prueba), enviar esos logs a servidores comerciales de terceros representaba un riesgo de fuga de datos inaceptable para un entorno de seguridad real.

**3 - Evidencia.** InvestigationEngine funcionando de punta a punta (ingesta de alerta, analisis, persistencia) con los datos procesados integramente dentro de la infraestructura propia, sin llamadas a servicios externos de IA. Principio de ingenieria invocado: minimizacion de superficie de exposicion de datos (confidencialidad).

**4 - Aporte personal.** Manuel modelo las entidades de dominio AlertEvent, VeredictoIA, Investigacion y FunctionCall (commit fe64e9e, `src/backend/orchestrator/src/main/java/ar/edu/ucp/soc/orchestrator/model/`) y construyo el pipeline AlertService + InvestigacionRepository (commit 751282d). Tomas instalo y configuro Sysmon y el Wazuh Agent, obteniendo la captura de los 433 eventos (commit aedea12, `.env.example`). Manuel realizo la integracion de OllamaClient (commit b74c382, `src/backend/orchestrator/src/main/java/ar/edu/ucp/soc/orchestrator/client/OllamaClient.java`) y, tras identificar el riesgo de OPSEC, el InvestigationEngine y el ajuste del SystemPrompt (commit b96b2aa, `src/backend/orchestrator/src/main/java/ar/edu/ucp/soc/orchestrator/service/InvestigationEngine.java` y `src/backend/orchestrator/src/main/resources/system_prompt.txt`).

**5 - Desacuerdo.** Tomas sugirio conectar el backend a la API de OpenAI para ganar potencia de razonamiento sin castigar el hardware local. Manuel sostuvo la posicion opuesta por el riesgo de OPSEC descripto en el punto 2. Se aplico la matriz de decision del Acta de Constitucion sobre riesgo de fuga de datos versus capacidad de razonamiento, y se acordo integrar OllamaClient sin necesidad de recurrir al desempate.

**6 - Herramienta auxiliar.** Ninguna. La integracion de Ollama y el ajuste del SystemPrompt se realizaron directamente sobre el codigo.

---

### 07/05/2026 -- Arquitectura y framework del backend

**1 - Decision.** Se adopta Java con el framework Spring Boot como tecnologia principal para orquestar la logica del motor de triage.

**2 - Alternativas y criterio de descarte.** Se evaluo (a) Python con FastAPI/Flask, estandar de la industria para scripts de seguridad y con conexion mas directa a modelos de IA, descartada; (b) Java con Spring Boot, adoptada. Criterio: el proyecto no se limitaba a un script de analisis, sino que requeria modelar entidades de dominio propias (AlertEvent, VeredictoIA, Investigacion) con una capa de persistencia estructurada y mantenible a largo plazo.

**3 - Evidencia.** Proyecto base estructurado en paquetes, con las dependencias (Web, JPA, H2, Ollama, Actuator) configuradas y funcionando, y los perfiles de entorno (dev) operativos desde esta misma sesion.

**4 - Aporte personal.** La decision de la arquitectura del backend, adopcion de Spring Boot y diseno de capas, fue realizada integramente por Manuel. Tomas genero el esqueleto inicial del proyecto con Spring Initializr y lo ejecuto sobre su maquina (commits 6bc2511, a06dbe4 y bae0bde, `src/backend/orchestrator/pom.xml` y `src/backend/orchestrator/src/main/resources/application.yaml`).

**5 - Desacuerdo.** Tomas impulsaba Python por estar el ecosistema de IA construido mayormente sobre ese lenguaje. Manuel sostuvo Java con Spring Boot, con el argumento de que el corazon del sistema era el pipeline de datos y no la IA en si. Se aplico la matriz de decision del Acta de Constitucion, comparando ambas opciones por mantenibilidad y ajuste al dominio del problema; no hizo falta el desempate de Manuel porque, al mostrar como Spring Data JPA resolvia de forma tipada la interaccion con H2, Tomas acordo con el criterio de Manuel.

**6 - Herramienta auxiliar.** Spring Initializr, para generar el esqueleto del proyecto con las dependencias exactas (Web, JPA, H2, Ollama, Actuator).

---

### 30/04/2026 -- Despliegue de infraestructura central (Wazuh)

**1 - Decision.** Se adopta Docker Compose, sobre el kit oficial wazuh-docker (v4.7.3), para el despliegue del stack de Wazuh, reemplazando la instalacion nativa con la que se hicieron las pruebas iniciales.

**2 - Alternativas y criterio de descarte.** Se evaluo (a) instalacion nativa sobre el sistema operativo, con la que se hicieron las primeras pruebas del entorno de monitorizacion, descartada; (b) Docker Compose con el kit oficial wazuh-docker, adoptada. Criterio: mantener el stack (indexer, manager, dashboard) ordenado y reproducible, algo que se volvio determinante apenas se empezo a sumar la integracion con la capa de IA sobre el mismo entorno.

**3 - Evidencia.** Stack de Wazuh operativo mediante docker-compose.wazuh.yml y el kit wazuh-docker-4.7.3, con los certificados SSL entre indexer, manager y dashboard generados y validados correctamente dentro de ese entorno en contenedores.

**4 - Aporte personal.** Tomas propuso migrar de la instalacion nativa a Docker Compose, y ejecuto esa migracion y la configuracion de los tres componentes de Wazuh sobre la maquina de la facultad, bajo su cuenta de GitHub (commits 68a291f, 9934051 y 78f0ab1, `src/wazuh-docker-4.7.3/` y `src/docker-compose.wazuh.yml`). Manuel valido que el cambio no comprometiera la estabilidad de los logs y acompano la verificacion de los certificados generados.

**5 - Desacuerdo.** No hubo desacuerdo entre los coautores: Tomas propuso el cambio de instalacion nativa a Docker Compose una vez comprobado, en las pruebas iniciales, que la contenerizacion iba a facilitar ordenar el stack a medida que se sumaran mas componentes (Wazuh y, mas adelante, la integracion con IA), y Manuel coincidio con el criterio sin necesidad de aplicar la matriz de decision del Acta de Constitucion.

**6 - Herramienta auxiliar.** Docker Compose y el kit oficial wazuh-docker (repositorio de Wazuh) como base de la configuracion del stack en contenedores.

---

**Nota de cierre.** El trabajo de desarrollo backend posterior al 17 de junio (Sprint 3-4: US-018, US-042, correccion de codificacion UTF-8, regeneracion de credencial de VirusTotal expuesta) todavia no esta pusheado al repositorio remoto al momento de esta entrega, y por lo tanto no tiene entrada propia en esta bitacora. Se incorporara arriba de todo, con su fecha correspondiente, una vez subido.
