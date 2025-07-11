CREATE TABLE document (
    id SERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,              -- nome generato internamente (es: 169938123_doc.pdf)
    content_type VARCHAR(100),                   -- tipo MIME (application/pdf, image/png, ...)
    owner_id VARCHAR(100) NOT NULL,                    -- ID utente che ha caricato il file
    tenant_id BIGINT NOT NULL,                   -- ID tenant
    status VARCHAR(50) DEFAULT 'IN_REVISIONE',   -- stato documento
    path TEXT,                                    -- path/id del documento su sistema di archiviazione esterno
    data_inserimento TIMESTAMP DEFAULT now(),    -- data inserimento
	data_modifica TIMESTAMP DEFAULT now()		 -- data modifica
);

-- 1. Definizione flusso
CREATE TABLE workflow_definition (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    tenant_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('SEQUENZIALE', 'PARALLELO'))
);

-- 2. Step approvativi (lista utenti)
CREATE TABLE workflow_step (
    id SERIAL PRIMARY KEY,
    workflow_definition_id BIGINT NOT NULL REFERENCES workflow_definition(id) ON DELETE CASCADE,
    approver_id VARCHAR(100) NOT NULL, -- id dell'utente approvatore
    step_order INT NOT NULL      -- ordine nello step (per SEQUENZIALE)
);

-- 3. Istanza di flusso per un documento
CREATE TABLE workflow_instance (
    id SERIAL PRIMARY KEY,
    document_id BIGINT NOT null REFERENCES document(id),
    workflow_definition_id BIGINT NOT NULL REFERENCES workflow_definition(id),
    tenant_id BIGINT NOT NULL,
    status VARCHAR(30) DEFAULT 'IN_ATTESA' CHECK (status IN ('IN_ATTESA', 'IN_CORSO', 'APPROVATO', 'RIFIUTATO')),
    started_at TIMESTAMP DEFAULT now(),
    completed_at TIMESTAMP
);

-- 4. Stato delle approvazioni
CREATE TABLE approval (
    id SERIAL PRIMARY KEY,
    workflow_instance_id BIGINT NOT NULL REFERENCES workflow_instance(id) ON DELETE CASCADE,
    approver_id VARCHAR(100) NOT NULL,
    status VARCHAR(20) DEFAULT 'IN_ATTESA' CHECK (status IN ('IN_ATTESA', 'APPROVATO', 'RIFIUTATO')),
    comment TEXT,
    approved_at TIMESTAMP,
    step_order INT -- utile per i flussi sequenziali
);