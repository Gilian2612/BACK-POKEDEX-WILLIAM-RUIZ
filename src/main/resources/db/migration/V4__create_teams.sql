CREATE TABLE teams (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    user_id     BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP
);

CREATE TABLE team_members (
    id           BIGSERIAL PRIMARY KEY,
    team_id      BIGINT      NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    pokemon_id   INTEGER     NOT NULL,
    pokemon_name VARCHAR(50) NOT NULL,
    slot         INTEGER     NOT NULL,
    UNIQUE (team_id, slot)
);

CREATE INDEX idx_team_user ON teams(user_id);
