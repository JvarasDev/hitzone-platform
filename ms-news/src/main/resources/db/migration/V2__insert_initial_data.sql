-- V2__insert_initial_data.sql
-- Datos iniciales para el microservicio ms-news
-- Propósito: poblar los primeros artículos de noticias de la plataforma.
--
-- Puesto que no hay un índice UNIQUE sobre 'title', usamos una consulta
-- condicional WHERE NOT EXISTS para evitar duplicados en ejecuciones sucesivas.

INSERT INTO news_articles (title, summary, content, category, author, image_url, published, created_at, updated_at)
SELECT 'Lanzamiento del Parche 8.0', 
       'Resumen de las notas del parche de balance general.', 
       'El parche 8.0 introduce cambios clave en el meta de los agentes, incluyendo ajustes en la duración de las habilidades de humo de Omen y el dash de Jett. Además, se añade la nueva rotación de mapas competitivos.', 
       'PATCH_NOTES', 
       'HitzoneTeam', 
       'https://example.com/images/patch-8.0.jpg', 
       true, 
       NOW(), 
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM news_articles WHERE title = 'Lanzamiento del Parche 8.0');

INSERT INTO news_articles (title, summary, content, category, author, image_url, published, created_at, updated_at)
SELECT 'Torneo de Invierno HitZone', 
       '¡Inscríbete ya con tu equipo en el torneo anual de la comunidad!', 
       'Este fin de semana se celebrará el torneo anual de invierno de la comunidad HitZone. Los ganadores recibirán aspectos exclusivos y puntos en el ranking de la plataforma. El registro estará abierto hasta el viernes a medianoche.', 
       'COMMUNITY', 
       'Coordinador del Torneo', 
       'https://example.com/images/tournament.jpg', 
       true, 
       NOW(), 
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM news_articles WHERE title = 'Torneo de Invierno HitZone');

INSERT INTO news_articles (title, summary, content, category, author, image_url, published, created_at, updated_at)
SELECT 'Nueva Rotación de Mapas Competitivos', 
       'Entérate de los mapas que entran y salen de la cola activa.', 
       'A partir del lunes, Ascent y Haven vuelven a la cola competitiva oficial, reemplazando a Icebox y Split. Prepárate y repasa tus tácticas de juego.', 
       'GENERAL', 
       'HitzoneTeam', 
       'https://example.com/images/maps-rotation.jpg', 
       true, 
       NOW(), 
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM news_articles WHERE title = 'Nueva Rotación de Mapas Competitivos');
