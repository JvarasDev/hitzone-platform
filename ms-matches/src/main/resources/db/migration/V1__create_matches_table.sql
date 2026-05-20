CREATE TABLE IF NOT EXISTS matches (
    id          BIGSERIAL PRIMARY KEY,
    match_id    VARCHAR(100) NOT NULL UNIQUE,
    map_name    VARCHAR(100),
    game_mode   VARCHAR(50)  NOT NULL DEFAULT 'COMPETITIVE',
    result      VARCHAR(10)  NOT NULL,   -- WIN / LOSS / DRAW
    score_team  INT,
    score_enemy INT,
    duration_s  INT,
    played_at   TIMESTAMP    DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS match_players (
    id          BIGSERIAL PRIMARY KEY,
    match_id    BIGINT       NOT NULL,
    username    VARCHAR(100) NOT NULL,
    agent_name  VARCHAR(100),
    kills       INT          DEFAULT 0,
    deaths      INT          DEFAULT 0,
    assists     INT          DEFAULT 0,
    rr_change   INT          DEFAULT 0,
    CONSTRAINT fk_mp_match FOREIGN KEY (match_id)
        REFERENCES matches (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_mp_username ON match_players (username);
CREATE INDEX IF NOT EXISTS idx_mp_match    ON match_players (match_id);