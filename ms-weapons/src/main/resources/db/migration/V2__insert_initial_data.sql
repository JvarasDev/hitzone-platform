-- V2__insert_initial_data.sql
-- Datos iniciales para el microservicio ms-weapons
-- Propósito: poblar el catálogo de armas de la plataforma de forma automática.
--
-- Categorías válidas (definidas en WeaponCategory.java):
--   RIFLE, PISTOL, SMG, SNIPER, SHOTGUN, HEAVY
--
-- ON CONFLICT (name) DO NOTHING garantiza idempotencia.

INSERT INTO weapon (name, category, damage, fire_rate, magazine_size, price, description, created_at, updated_at)
VALUES
    ('Classic',  'PISTOL',  26.0,  6.75,  12,  0,    'Pistola inicial estándar y gratuita.', NOW(), NOW()),
    ('Ghost',    'PISTOL',  30.0,  6.75,  15,  500,  'Pistola silenciosa ideal para rondas de pistolas.', NOW(), NOW()),
    ('Sheriff',  'PISTOL',  55.0,  4.00,  6,   800,  'Revólver de alto calibre capaz de matar de un tiro a la cabeza.', NOW(), NOW()),
    ('Spectre',  'SMG',     26.0,  13.33, 30,  1600, 'Subfusil silenciado versátil para media distancia.', NOW(), NOW()),
    ('Bucky',    'SHOTGUN', 22.0,  1.10,  5,   850,  'Escopeta de bombeo económica para distancias cortas.', NOW(), NOW()),
    ('Vandal',   'RIFLE',   40.0,  9.75,  25,  2900, 'Rifle de asalto de alto daño constante a cualquier rango.', NOW(), NOW()),
    ('Phantom',  'RIFLE',   39.0,  11.00, 30,  2900, 'Rifle de asalto silenciado y estable sin trazadoras.', NOW(), NOW()),
    ('Operator', 'SNIPER',  150.0, 0.60,  5,   4700, 'Rifle de francotirador pesado letal de un impacto al cuerpo.', NOW(), NOW()),
    ('Odin',     'HEAVY',   95.0,  12.00, 100, 3200, 'Ametralladora pesada con gran penetración de pared.', NOW(), NOW())
ON CONFLICT (name) DO NOTHING;
