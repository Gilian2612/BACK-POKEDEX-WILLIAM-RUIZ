CREATE TABLE favorite_pokemon (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    pokemon_id   INTEGER     NOT NULL,
    pokemon_name VARCHAR(50) NOT NULL,
    favorited_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, pokemon_id)
);

CREATE INDEX idx_favorite_user ON favorite_pokemon(user_id);