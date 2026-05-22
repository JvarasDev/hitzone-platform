-- V2__insert_initial_data.sql
-- Datos iniciales para el microservicio ms-rank
-- Propósito: garantizar que usuarios base existan en la BD aislada de rankings
-- desde el primer arranque, evitando 404 en consultas legítimas.
--
-- Regla de ranking (definida en PlayerRankServiceImpl.applyRank):
--   rr_points >= 1000 → RADIANT   (rank_number = 7)
--   rr_points >= 800  → DIAMOND   (rank_number = 6)
--   rr_points >= 600  → PLATINUM  (rank_number = 5)
--   rr_points >= 400  → GOLD      (rank_number = 4)
--   rr_points >= 200  → SILVER    (rank_number = 3)
--   rr_points >= 100  → BRONZE    (rank_number = 2)
--   rr_points <  100  → IRON      (rank_number = 1)
--
-- ON CONFLICT (username) DO NOTHING garantiza idempotencia:
-- si el registro ya existe (creado manualmente vía API), no falla.

INSERT INTO player_ranks (username, rank_name, rank_number, rr_points, wins, losses, updated_at)
VALUES
    -- Administrador del sistema — arranca en IRON/Unranked con 0 puntos
    ('admin',    'IRON',     1, 0,   0, 0, NOW()),

    -- Jugadores de demo para poblar el leaderboard en la UI
    ('jugador1', 'SILVER',   3, 250, 10, 5,  NOW()),
    ('jugador2', 'GOLD',     4, 420, 18, 7,  NOW()),
    ('jugador3', 'BRONZE',   2, 150, 5,  8,  NOW()),
    ('jugador4', 'PLATINUM', 5, 620, 25, 10, NOW()),
    ('jugador5', 'IRON',     1, 40,  1,  6,  NOW())

ON CONFLICT (username) DO NOTHING;
