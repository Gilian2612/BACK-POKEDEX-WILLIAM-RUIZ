CREATE TABLE captured_pokemon (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    pokemon_id   INTEGER     NOT NULL,
    pokemon_name VARCHAR(50) NOT NULL,
    captured_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, pokemon_id)
);

CREATE INDEX idx_captured_user ON captured_pokemon(user_id);