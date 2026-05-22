-- V2__insert_initial_data.sql
-- Datos iniciales para el microservicio ms-matches
-- Propósito: registrar las primeras partidas y estadísticas de jugadores de forma automatizada.
--
-- ON CONFLICT (match_id) DO NOTHING garantiza idempotencia para las partidas.
-- Para match_players, se utiliza WHERE NOT EXISTS para evitar duplicidad de registros.

-- 1. Insertar Partidas
INSERT INTO matches (match_id, map_name, game_mode, result, score_team, score_enemy, duration_s, played_at)
VALUES 
    ('match-abc123', 'Ascent', 'COMPETITIVE', 'WIN',  13, 9,  2450, NOW() - INTERVAL '2 hours'),
    ('match-def456', 'Bind',   'COMPETITIVE', 'LOSS', 10, 13, 2300, NOW() - INTERVAL '1 hours')
ON CONFLICT (match_id) DO NOTHING;

-- 2. Insertar Estadísticas de Jugadores de la Partida 1 ('match-abc123')
INSERT INTO match_players (match_id, username, agent_name, kills, deaths, assists, rr_change)
SELECT (SELECT id FROM matches WHERE match_id = 'match-abc123'), 'admin', 'Omen', 18, 15, 8, 20
WHERE NOT EXISTS (SELECT 1 FROM match_players WHERE match_id = (SELECT id FROM matches WHERE match_id = 'match-abc123') AND username = 'admin');

INSERT INTO match_players (match_id, username, agent_name, kills, deaths, assists, rr_change)
SELECT (SELECT id FROM matches WHERE match_id = 'match-abc123'), 'jugador1', 'Jett', 22, 18, 5, 20
WHERE NOT EXISTS (SELECT 1 FROM match_players WHERE match_id = (SELECT id FROM matches WHERE match_id = 'match-abc123') AND username = 'jugador1');

INSERT INTO match_players (match_id, username, agent_name, kills, deaths, assists, rr_change)
SELECT (SELECT id FROM matches WHERE match_id = 'match-abc123'), 'jugador2', 'Sage', 10, 18, 12, 15
WHERE NOT EXISTS (SELECT 1 FROM match_players WHERE match_id = (SELECT id FROM matches WHERE match_id = 'match-abc123') AND username = 'jugador2');

-- 3. Insertar Estadísticas de Jugadores de la Partida 2 ('match-def456')
INSERT INTO match_players (match_id, username, agent_name, kills, deaths, assists, rr_change)
SELECT (SELECT id FROM matches WHERE match_id = 'match-def456'), 'admin', 'Omen', 12, 17, 4, -15
WHERE NOT EXISTS (SELECT 1 FROM match_players WHERE match_id = (SELECT id FROM matches WHERE match_id = 'match-def456') AND username = 'admin');

INSERT INTO match_players (match_id, username, agent_name, kills, deaths, assists, rr_change)
SELECT (SELECT id FROM matches WHERE match_id = 'match-def456'), 'jugador1', 'Jett', 15, 18, 2, -15
WHERE NOT EXISTS (SELECT 1 FROM match_players WHERE match_id = (SELECT id FROM matches WHERE match_id = 'match-def456') AND username = 'jugador1');
