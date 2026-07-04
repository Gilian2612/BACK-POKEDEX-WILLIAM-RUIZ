-- ============================================================
-- WildDex - Migración V1: Esquema inicial
-- ============================================================

-- USUARIOS
CREATE TABLE IF NOT EXISTS usuarios (
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(20)  NOT NULL UNIQUE,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    foto_url    VARCHAR(500),
    activo      BOOLEAN      NOT NULL DEFAULT TRUE,
    creado_en   TIMESTAMP    NOT NULL DEFAULT NOW(),
    actualizado_en TIMESTAMP NOT NULL DEFAULT NOW()
);

-- POKÉMON CAPTURADOS
CREATE TABLE IF NOT EXISTS coleccion (
    id          BIGSERIAL PRIMARY KEY,
    usuario_id  BIGINT       NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    pokemon_id  INTEGER      NOT NULL,
    capturado_en TIMESTAMP   NOT NULL DEFAULT NOW(),
    UNIQUE (usuario_id, pokemon_id)
);

-- POKÉMON FAVORITOS
CREATE TABLE IF NOT EXISTS favoritos (
    id          BIGSERIAL PRIMARY KEY,
    usuario_id  BIGINT       NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    pokemon_id  INTEGER      NOT NULL,
    agregado_en TIMESTAMP    NOT NULL DEFAULT NOW(),
    UNIQUE (usuario_id, pokemon_id)
);

-- MERCADO DE POKÉMON
CREATE TABLE IF NOT EXISTS mercado (
    id              BIGSERIAL PRIMARY KEY,
    usuario_id      BIGINT       NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    pokemon_id      INTEGER      NOT NULL,
    descripcion     VARCHAR(200),
    activo          BOOLEAN      NOT NULL DEFAULT TRUE,
    publicado_en    TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- SOLICITUDES DE INTERCAMBIO
CREATE TABLE IF NOT EXISTS intercambios (
    id                  BIGSERIAL PRIMARY KEY,
    publicacion_id      BIGINT       NOT NULL REFERENCES mercado(id) ON DELETE CASCADE,
    solicitante_id      BIGINT       NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    pokemon_ofrecido_id INTEGER      NOT NULL,
    mensaje             VARCHAR(200),
    estado              VARCHAR(20)  NOT NULL DEFAULT 'PENDIENTE',
    -- Estado: PENDIENTE, ACEPTADO, RECHAZADO
    creado_en           TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_estado CHECK (estado IN ('PENDIENTE', 'ACEPTADO', 'RECHAZADO'))
);

-- ÍNDICES
CREATE INDEX IF NOT EXISTS idx_coleccion_usuario ON coleccion(usuario_id);
CREATE INDEX IF NOT EXISTS idx_favoritos_usuario ON favoritos(usuario_id);
CREATE INDEX IF NOT EXISTS idx_mercado_activo    ON mercado(activo);
CREATE INDEX IF NOT EXISTS idx_intercambios_pub  ON intercambios(publicacion_id);
