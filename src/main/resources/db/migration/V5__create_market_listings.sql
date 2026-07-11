#---- crea la tabla del mercado en PostgreSQL.
#---La tabla market_listings almacena cada publicación de venta: quién vende (seller_id), quién compró (buyer_id, null hasta que alguien compre), qué Pokémon, a qué precio en monedas, y el estado 
#-----(ACTIVE, SOLD, CANCELLED).
CREATE TABLE market_listings (
    id           BIGSERIAL PRIMARY KEY,
    seller_id    BIGINT      NOT NULL REFERENCES users(id),
    buyer_id     BIGINT               REFERENCES users(id),
    pokemon_id   INTEGER     NOT NULL,
    pokemon_name VARCHAR(50) NOT NULL,
    price        INTEGER     NOT NULL,
    status       VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at   TIMESTAMP   NOT NULL DEFAULT NOW(),
    sold_at      TIMESTAMP
);

CREATE INDEX idx_listing_status ON market_listings(status);
CREATE INDEX idx_listing_seller ON market_listings(seller_id);