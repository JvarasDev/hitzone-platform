-- V1: Creación inicial de la tabla audit_log
-- Microservicio: ms-persistence (log de auditoría del sistema)
-- Fecha: 2026-05-15

CREATE TABLE IF NOT EXISTS audit_log (
    id           BIGSERIAL PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    action       VARCHAR(100) NOT NULL,
    entity_type  VARCHAR(100),
    entity_id    BIGINT,
    username     VARCHAR(100),
    details      TEXT,
    status       VARCHAR(20) DEFAULT 'SUCCESS',
    created_at   TIMESTAMP   DEFAULT NOW()
);

-- Índice para consultas por servicio origen
CREATE INDEX IF NOT EXISTS idx_audit_service ON audit_log (service_name);

-- Índice para consultas por usuario
CREATE INDEX IF NOT EXISTS idx_audit_username ON audit_log (username);

-- Índice para consultas por fecha (reportes cronológicos)
CREATE INDEX IF NOT EXISTS idx_audit_created_at ON audit_log (created_at);
