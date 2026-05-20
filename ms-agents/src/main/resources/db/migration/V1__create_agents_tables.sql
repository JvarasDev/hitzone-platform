-- V1: Creación inicial de las tablas agents y abilities
-- Microservicio: ms-agents
-- Fecha: 2026-05-15

CREATE TABLE IF NOT EXISTS agents (
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(255) NOT NULL UNIQUE,
    role         VARCHAR(50)  NOT NULL,
    description  TEXT,
    image_url    VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS abilities (
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    type         VARCHAR(50)  NOT NULL,
    description  TEXT,
    agent_id     BIGINT NOT NULL,
    CONSTRAINT fk_ability_agent FOREIGN KEY (agent_id)
        REFERENCES agents (id)
        ON DELETE CASCADE
);

-- Índice para búsquedas de habilidades por agente
CREATE INDEX IF NOT EXISTS idx_ability_agent_id ON abilities (agent_id);

-- Índice para búsquedas de agentes por rol (endpoint: GET /api/agents/reports/by-role)
CREATE INDEX IF NOT EXISTS idx_agent_role ON agents (role);
