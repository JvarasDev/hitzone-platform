-- V1: Creación inicial de la tabla weapon
-- Microservicio: ms-weapons
-- Fecha: 2026-05-15

CREATE TABLE IF NOT EXISTS weapon (
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(255) NOT NULL UNIQUE,
    category       VARCHAR(50)  NOT NULL,
    damage         NUMERIC(10, 2),
    fire_rate      NUMERIC(10, 2),
    magazine_size  INTEGER,
    price          INTEGER,
    description    TEXT,
    created_at     TIMESTAMP DEFAULT NOW(),
    updated_at     TIMESTAMP DEFAULT NOW()
);

-- Índice para búsquedas por categoría (endpoint: GET /api/weapons/category/{cat})
CREATE INDEX IF NOT EXISTS idx_weapon_category ON weapon (category);
