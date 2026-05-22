-- V2__insert_initial_data.sql
-- Datos iniciales para el microservicio ms-persistence
-- Propósito: poblar los primeros registros de auditoría del sistema para pruebas.
--
-- Puesto que no hay índice UNIQUE en audit_log, usamos WHERE NOT EXISTS
-- para evitar la duplicidad al reiniciar servicios.

INSERT INTO audit_log (service_name, action, entity_type, entity_id, username, details, status, created_at)
SELECT 'ms-auth', 'LOGIN', 'User', 1, 'admin', 'Inicio de sesión exitoso del administrador del sistema.', 'SUCCESS', NOW()
WHERE NOT EXISTS (SELECT 1 FROM audit_log WHERE username = 'admin' AND action = 'LOGIN' AND service_name = 'ms-auth');

INSERT INTO audit_log (service_name, action, entity_type, entity_id, username, details, status, created_at)
SELECT 'ms-weapons', 'CREATE_WEAPON', 'Weapon', 1, 'admin', 'Arma Vandal registrada exitosamente en el catálogo.', 'SUCCESS', NOW()
WHERE NOT EXISTS (SELECT 1 FROM audit_log WHERE action = 'CREATE_WEAPON' AND entity_id = 1);

INSERT INTO audit_log (service_name, action, entity_type, entity_id, username, details, status, created_at)
SELECT 'ms-agents', 'CREATE_AGENT', 'Agent', 1, 'admin', 'Agente Jett registrado exitosamente con sus habilidades iniciales.', 'SUCCESS', NOW()
WHERE NOT EXISTS (SELECT 1 FROM audit_log WHERE action = 'CREATE_AGENT' AND entity_id = 1);
