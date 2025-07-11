# TrustFlow - Piattaforma di gestione documentale e approvazione

Elaborato tecnico sistema TrustFlow

## 🧩 Struttura del progetto

| Microservizio        | Descrizione                                                                 |
|----------------------|------------------------------------------------------------------------------|
| `auth-service`       | Servizio di autenticazione utenti via username/password e login SPID mock  |
| `document-service`   | Upload, visualizzazione e gestione flusso di approvazione documenti su Postgres       |
| `integration-layer`  | Mock di servizi esterni (es. firma remota, conservazione) autenticati via OAuth2 |
| `audit-service`      | Servizio per la tracciabilità e auditing centralizzato su MongoDB   |

---

## 🔐 Sicurezza

- **JWT**: utilizzato per autenticare l'accesso alle API protette (`document-service`)
- **OAuth2 (Authorization Server)**: implementato localmente per `integration-layer` e accesso token-based
- **SPID Mock**: endpoint fake per simulare flusso login federato

---

## 🚀 Tecnologie principali

- **Spring Boot 3.x**
- **Spring Security + OAuth2 Authorization Server**
- **RestTemplate con JWT propagation**
- **PostgreSQL via Docker (con Docker Compose)**
- **Maven multi-servizio (moduli separati)**
- **Log JSON compatibile ELK (Logstash Encoder)**

---

## ▶️ Database Postgres e Mongo

- Avviati localmente su wsl con docker (docker-compose.yml)