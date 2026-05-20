-- V1: Creación inicial de la tabla maps
-- Microservicio: ms-maps
-- Fecha: 2026-05-15

CREATE TABLE IF NOT EXISTS maps (
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(255) NOT NULL UNIQUE,
    type         VARCHAR(50),
    difficulty   VARCHAR(50),
    description  TEXT,
    total_site   INTEGER DEFAULT 0,
    active       BOOLEAN DEFAULT TRUE,
    created_at   TIMESTAMP DEFAULT NOW(),
    updated_at   TIMESTAMP DEFAULT NOW()
);

-- Índice para filtrar mapas activos
CREATE INDEX IF NOT EXISTS idx_maps_active ON maps (active);

-- Índice para filtrar por dificultad (endpoint: GET /api/maps/reports/by-difficulty)
CREATE INDEX IF NOT EXISTS idx_maps_difficulty ON maps (difficulty);
