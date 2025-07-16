-- Creazione tabella tenant
CREATE TABLE tenant (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL
);

-- Creazione tabella utente
CREATE TABLE utente (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    codice_fiscale VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50),
    tenant_id INTEGER NOT NULL,
    CONSTRAINT fk_user_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id),
    CONSTRAINT uq_username_per_tenant UNIQUE (username, tenant_id)
);