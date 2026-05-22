-- V2__insert_initial_data.sql
-- Datos iniciales para el microservicio ms-agents
-- Propósito: poblar los agentes y sus respectivas habilidades básicas.
--
-- Roles válidos (definidos en AgentRole.java):
--   DUELIST, CONTROLLER, SENTINEL, INITIATOR
--
-- Tipos de habilidad válidos (definidos en AbilityType.java):
--   Q, E, C, ULTIMATE

-- 1. Insertar Agentes base (Idempotente)
INSERT INTO agents (name, role, description, image_url)
VALUES
    ('Jett', 'DUELIST', 'Representando a su patria, Corea del Sur, Jett tiene un estilo de lucha ágil y evasivo que le permite correr riesgos como nadie.', 'https://images.contentstack.io/v3/assets/blt73321487d6f1119d/bltdbff4c4c520d99e9/5ebd0375cf61302f143490d5/TX_Jett_PlayCards.png'),
    ('Omen', 'CONTROLLER', 'Un fantasma del pasado, Omen caza en las sombras. Ciega a los enemigos, se teletransporta por el campo de batalla y deja que la paranoia los consuma.', 'https://images.contentstack.io/v3/assets/blt73321487d6f1119d/blt4eef68d6d6d13d7e/5ebd0375cf61302f143490d5/TX_Omen_PlayCards.png'),
    ('Sage', 'SENTINEL', 'El baluarte de China, Sage proporciona seguridad para ella y su equipo dondequiera que vaya. Capaz de revivir a sus aliados caídos y repeler ataques agresivos.', 'https://images.contentstack.io/v3/assets/blt73321487d6f1119d/blt4eef68d6d6d13d7e/5ebd0375cf61302f143490d5/TX_Sage_PlayCards.png')
ON CONFLICT (name) DO NOTHING;

-- 2. Insertar Habilidades de Jett (Idempotente por subconsulta)
INSERT INTO abilities (name, type, description, agent_id)
SELECT 'Cloudburst', 'C', 'Lanza instantáneamente un proyectil que se expande en una nube que bloquea la visión al impactar contra una superficie.', id
FROM agents WHERE name = 'Jett'
AND NOT EXISTS (SELECT 1 FROM abilities WHERE name = 'Cloudburst' AND agent_id = (SELECT id FROM agents WHERE name = 'Jett'));

INSERT INTO abilities (name, type, description, agent_id)
SELECT 'Updraft', 'Q', 'Lanza instantáneamente a Jett hacia el cielo.', id
FROM agents WHERE name = 'Jett'
AND NOT EXISTS (SELECT 1 FROM abilities WHERE name = 'Updraft' AND agent_id = (SELECT id FROM agents WHERE name = 'Jett'));

INSERT INTO abilities (name, type, description, agent_id)
SELECT 'Tailwind', 'E', 'Lanza instantáneamente a Jett en la dirección en la que se está moviendo.', id
FROM agents WHERE name = 'Jett'
AND NOT EXISTS (SELECT 1 FROM abilities WHERE name = 'Tailwind' AND agent_id = (SELECT id FROM agents WHERE name = 'Jett'));

INSERT INTO abilities (name, type, description, agent_id)
SELECT 'Blade Storm', 'ULTIMATE', 'Equipa un conjunto de cuchillos arrojadizos altamente precisos que se recargan al matar a un oponente.', id
FROM agents WHERE name = 'Jett'
AND NOT EXISTS (SELECT 1 FROM abilities WHERE name = 'Blade Storm' AND agent_id = (SELECT id FROM agents WHERE name = 'Jett'));

-- 3. Insertar Habilidades de Omen (Idempotente por subconsulta)
INSERT INTO abilities (name, type, description, agent_id)
SELECT 'Shrouded Step', 'C', 'Equipa una habilidad de teletransporte y ve su indicador de alcance.', id
FROM agents WHERE name = 'Omen'
AND NOT EXISTS (SELECT 1 FROM abilities WHERE name = 'Shrouded Step' AND agent_id = (SELECT id FROM agents WHERE name = 'Omen'));

INSERT INTO abilities (name, type, description, agent_id)
SELECT 'Paranoia', 'Q', 'Dispara instantáneamente un proyectil de sombra hacia adelante, cegando brevemente a todos los jugadores que toca.', id
FROM agents WHERE name = 'Omen'
AND NOT EXISTS (SELECT 1 FROM abilities WHERE name = 'Paranoia' AND agent_id = (SELECT id FROM agents WHERE name = 'Omen'));

INSERT INTO abilities (name, type, description, agent_id)
SELECT 'Dark Cover', 'E', 'Equipa una esfera de sombra y ve su indicador de alcance. Dispara para lanzar la esfera.', id
FROM agents WHERE name = 'Omen'
AND NOT EXISTS (SELECT 1 FROM abilities WHERE name = 'Dark Cover' AND agent_id = (SELECT id FROM agents WHERE name = 'Omen'));

INSERT INTO abilities (name, type, description, agent_id)
SELECT 'From the Shadows', 'ULTIMATE', 'Equipa un mapa táctico. Dispara para comenzar a teletransportarte a cualquier lugar del mapa.', id
FROM agents WHERE name = 'Omen'
AND NOT EXISTS (SELECT 1 FROM abilities WHERE name = 'From the Shadows' AND agent_id = (SELECT id FROM agents WHERE name = 'Omen'));

-- 4. Insertar Habilidades de Sage (Idempotente por subconsulta)
INSERT INTO abilities (name, type, description, agent_id)
SELECT 'Barrier Orb', 'C', 'Equipa un orbe de barrera. Dispara para colocar una pared sólida.', id
FROM agents WHERE name = 'Sage'
AND NOT EXISTS (SELECT 1 FROM abilities WHERE name = 'Barrier Orb' AND agent_id = (SELECT id FROM agents WHERE name = 'Sage'));

INSERT INTO abilities (name, type, description, agent_id)
SELECT 'Slow Orb', 'Q', 'Equipa un orbe de ralentización. Dispara para lanzarlo, creando un campo que ralentiza a los jugadores.', id
FROM agents WHERE name = 'Sage'
AND NOT EXISTS (SELECT 1 FROM abilities WHERE name = 'Slow Orb' AND agent_id = (SELECT id FROM agents WHERE name = 'Sage'));

INSERT INTO abilities (name, type, description, agent_id)
SELECT 'Healing Orb', 'E', 'Equipa un orbe de curación. Dispara con la mira puesta en un aliado dañado para curarlo.', id
FROM agents WHERE name = 'Sage'
AND NOT EXISTS (SELECT 1 FROM abilities WHERE name = 'Healing Orb' AND agent_id = (SELECT id FROM agents WHERE name = 'Sage'));

INSERT INTO abilities (name, type, description, agent_id)
SELECT 'Resurrection', 'ULTIMATE', 'Equipa una habilidad de resurrección. Dispara sobre un aliado muerto para revivirlo.', id
FROM agents WHERE name = 'Sage'
AND NOT EXISTS (SELECT 1 FROM abilities WHERE name = 'Resurrection' AND agent_id = (SELECT id FROM agents WHERE name = 'Sage'));
