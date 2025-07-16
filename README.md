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


# ✅ Guida rapida – Test End-to-End della piattaforma TrustFlow

Questa guida ti accompagna passo passo nell’utilizzo base della piattaforma:

1. 🔐 Autenticazione utente con JWT  
2. 📤 Caricamento documento  
3. 🔁 Avvio workflow approvativo  
4. ✅ Approvazione documento da parte di un approver  

---

## 🔧 Prerequisiti

- Servizi avviati:
  - `auth-service` → http://localhost:9000
  - `document-service` → http://localhost:9001
  - `audit-service` → http://localhost:9010
- Database PostgreSQL e Mongo attivi (docker-compose.yml)
- RabbitMQ attivo (docker-compose.yml)
- Strumento per testing API: Postman o curl

---

## Esempio di test JWT

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
	