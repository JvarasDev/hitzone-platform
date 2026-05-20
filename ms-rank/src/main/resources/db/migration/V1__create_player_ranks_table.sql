-- V1__create_player_ranks_table.sql
-- Migración inicial: tabla de rankings de jugadores

CREATE TABLE IF NOT EXISTS player_ranks (
    id          BIGSERIAL       PRIMARY KEY,
    username    VARCHAR(100)    NOT NULL UNIQUE,
    rank_name   VARCHAR(50)     NOT NULL,
    rank_number INTEGER         NOT NULL,
    rr_points   INTEGER         NOT NULL DEFAULT 0,
    wins        INTEGER         NOT NULL DEFAULT 0,
    losses      INTEGER         NOT NULL DEFAULT 0,
    updated_at  TIMESTAMP
);
