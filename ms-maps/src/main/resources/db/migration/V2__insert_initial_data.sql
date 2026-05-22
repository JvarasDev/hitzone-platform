-- V2__insert_initial_data.sql
-- Datos iniciales para el microservicio ms-maps
-- Propósito: poblar los mapas iniciales del juego.
--
-- Tipos de mapa válidos (definidos en typeMaps.java):
--   COMPETITIVE, UNRATED, CUSTOM
--
-- Dificultades válidas (definidas en typeDifficulty.java):
--   EASY, MEDIUM, HARD
--
-- ON CONFLICT (name) DO NOTHING garantiza la idempotencia.

INSERT INTO maps (name, type, difficulty, description, total_site, active, created_at, updated_at)
VALUES
    ('Ascent',      'COMPETITIVE', 'MEDIUM', 'Mapa de estilo veneciano clásico con una zona central abierta y puertas metálicas interactivas.', 2, true, NOW(), NOW()),
    ('Bind',        'COMPETITIVE', 'MEDIUM', 'Mapa ubicado en un desierto que destaca por sus teletransportadores de una sola dirección.', 2, true, NOW(), NOW()),
    ('Haven',       'COMPETITIVE', 'HARD',   'Mapa característico por poseer tres sitios de bomba en lugar de los dos habituales.', 3, true, NOW(), NOW()),
    ('Split',       'COMPETITIVE', 'MEDIUM', 'Mapa urbano enfocado en el control de zonas elevadas mediante el uso de cuerdas.', 2, true, NOW(), NOW()),
    ('Icebox',      'UNRATED',     'HARD',   'Instalación ártica abandonada que introduce tirolesas horizontales y terrenos muy verticales.', 2, true, NOW(), NOW()),
    ('Range',       'CUSTOM',      'EASY',   'Mapa de entrenamiento para practicar puntería, pruebas de tiro y habilidades.', 1, true, NOW(), NOW())
ON CONFLICT (name) DO NOTHING;
