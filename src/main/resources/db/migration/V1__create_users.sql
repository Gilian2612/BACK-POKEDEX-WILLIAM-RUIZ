CREATE TABLE users (id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password        VARCHAR(255),
    profile_image_url VARCHAR(500),
    role            VARCHAR(20)  NOT NULL DEFAULT 'USER',
    provider        VARCHAR(20)  NOT NULL DEFAULT 'LOCAL',
    provider_id     VARCHAR(255),
    otp_code        VARCHAR(6),
    otp_expiry      TIMESTAMP,
    email_verified  BOOLEAN      NOT NULL DEFAULT FALSE,
    enabled         BOOLEAN      NOT NULL DEFAULT TRUE,
    coins           INTEGER      NOT NULL DEFAULT 1000,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP);


