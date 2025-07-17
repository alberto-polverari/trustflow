# TrustFlow

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
- **PostgreSQL via Docker **
- **MongoDB via Docker **
- **RabbitMQ via Docker **
- **Maven **
- **Logback (JSON - Logstash Encoder)**

---

## 🔧 Test End-to-End locale

- Servizi avviati:
  - `auth-service` → http://localhost:8081
  - `document-service` → http://localhost:8082
  - `audit-service` → http://localhost:8083
  - `integration-layer` → http://localhost:9000
- Database PostgreSQL e Mongo attivi (docker-compose.yml)
- RabbitMQ attivo (docker-compose.yml)
- Strumento per testing API: Postman

---

 1. Login user/pass:
		POST http://localhost:8081/api/auth/login
		Body:
		{
		  "username": "user1",
		  "password": "admin"
		}
	L'api ritorna il token JWT da utilizzare per le altre chiamate.
 2. Upload documento:
		POST http://localhost:8082/api/documents/uploadMock?filename={filename}
	L'api inserisce un record di test nella tabella dei documenti e ne ritorna l'oggetto.
 3. Avvio workflow:
		POST http://localhost:8082/api/workflow/startRevisione?documentId={documentId}
 4. Approvazione utente:
		POST http://localhost:8082/api/workflow/approva?documentId={documentId}
		Body:
		{
		  "documentId": Long,
		  "comment": String,
		  "approved": boolean
		}
	