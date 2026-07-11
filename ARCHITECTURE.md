# Documento de Arquitectura — WildDex API

**Proyecto:** WildDex Pokédex REST API  
**Materia:** Desarrollo y Operaciones de Software (DOSW) — 2026 Intersemestral  
**Autor:** William Santiago Ruiz Medina  
**Versión:** 0.0.1-SNAPSHOT  

---

## 1. Visión General

WildDex es una API REST construida con **Spring Boot 4.1** y **Java 21** que sirve como backend para una aplicación móvil de Pokédex. El sistema permite a los usuarios explorar el catálogo completo de Pokémon (consumiendo la PokéAPI v2 en tiempo real), gestionar colecciones personales de capturas y favoritos, armar equipos, comparar estadísticas y comerciar Pokémon a través de un mercado interno con sistema de monedas. Incluye autenticación con OTP por email, login con Google OAuth2 y un chat de soporte potenciado por IA (Claude).

---

## 2. Stack Tecnológico

| Capa | Tecnología | Versión |
|---|---|---|
| Lenguaje | Java | 21 |
| Framework | Spring Boot | 4.1.0 |
| Base de datos | PostgreSQL | 15 (Alpine) |
| Migraciones | Flyway | — |
| ORM | Spring Data JPA + Hibernate | — |
| Seguridad | Spring Security + JWT (JJWT 0.12.6) | — |
| OAuth2 | Spring OAuth2 Client (Google) | — |
| Cliente HTTP | Spring WebFlux (WebClient) | — |
| Caché | Spring Cache + Caffeine | — |
| Email | Spring Mail (SMTP/Gmail) | — |
| Mapeo DTO | MapStruct 1.5.5 + Lombok 1.18.34 | — |
| Documentación | SpringDoc OpenAPI (Swagger UI) 2.8.8 | — |
| Monitoreo | Spring Actuator | — |
| Cobertura | JaCoCo 0.8.11 (mínimo 70%) | — |
| Contenedores | Docker Compose 3.8 | — |
| IA (Soporte) | Anthropic API (Claude Haiku) | — |

---

## 3. Arquitectura de Capas

El proyecto sigue una **arquitectura de capas clásica** (Controller → Service → Repository) dentro de un monolito modular organizado por dominio funcional.

```
┌─────────────────────────────────────────────────────┐
│                    CLIENTES                         │
│              (App Móvil / Swagger UI)               │
└──────────────────────┬──────────────────────────────┘
                       │ HTTP/REST (JSON)
                       ▼
┌─────────────────────────────────────────────────────┐
│               SPRING SECURITY                       │
│         JWT Filter · CORS · OAuth2 Google           │
└──────────────────────┬──────────────────────────────┘
                       ▼
┌─────────────────────────────────────────────────────┐
│                 CONTROLLERS                         │
│  Auth · User · Pokemon · Collection · Team          │
│  Compare · Market · Admin · Support                 │
└──────────────────────┬──────────────────────────────┘
                       ▼
┌─────────────────────────────────────────────────────┐
│                  SERVICES                           │
│  AuthService · UserService · PokemonService         │
│  CollectionService · TeamService · CompareService   │
│  MarketService · AdminService · SupportService      │
│  PokeApiClient (WebClient → PokéAPI v2)             │
└────────┬─────────────────────────────┬──────────────┘
         ▼                             ▼
┌──────────────────┐     ┌────────────────────────────┐
│   REPOSITORIES   │     │    INTEGRACIONES EXTERNAS  │
│  (Spring Data    │     │  · PokéAPI v2 (WebClient)  │
│   JPA)           │     │  · Google OAuth2           │
│                  │     │  · SMTP (OTP por email)    │
│                  │     │  · Anthropic API (Claude)  │
└────────┬─────────┘     └────────────────────────────┘
         ▼
┌──────────────────┐
│   PostgreSQL 15  │
│   (Docker)       │
└──────────────────┘
```

---

## 4. Estructura del Proyecto

```
src/main/java/com/wilddex/
├── config/                  # Configuraciones de Spring
│   ├── CorsConfig.java      # Orígenes permitidos (localhost:3000, 5173)
│   ├── SecurityConfig.java  # Cadena de filtros, endpoints públicos/protegidos
│   ├── SwaggerConfig.java   # Metadatos OpenAPI
│   └── WebClientConfig.java # Bean de WebClient para PokéAPI
│
├── controller/              # Endpoints REST (capa de presentación)
│   ├── AuthController.java       # /api/v1/auth/**
│   ├── UserController.java       # /api/v1/users/**
│   ├── PokemonController.java    # /api/v1/pokemon/**
│   ├── CollectionController.java # /api/v1/collection/**
│   ├── TeamController.java       # /api/v1/teams/**
│   ├── CompareController.java    # /api/v1/pokemon/compare
│   ├── MarketController.java     # /api/v1/market/**
│   ├── AdminController.java      # /api/v1/admin/** (ROLE_ADMIN)
│   └── SupportController.java    # /api/v1/support/**
│
├── dto/                     # Objetos de transferencia de datos
│   ├── request/             # Payloads de entrada (Login, Register, etc.)
│   ├── response/            # Payloads de salida (ApiResponse<T>, etc.)
│   ├── admin/               # DTOs del módulo de administración
│   ├── market/              # DTOs del mercado
│   └── support/             # DTOs del chat de soporte IA
│
├── exception/               # Manejo global de excepciones
│   ├── GlobalExceptionHandler.java
│   ├── BadRequestException.java
│   ├── ConflictException.java
│   ├── ForbiddenException.java
│   ├── ResourceNotFoundException.java
│   └── UnauthorizedException.java
│
├── mapper/                  # MapStruct mappers (Entity ↔ DTO)
│   ├── CollectionMapper.java
│   ├── TeamMapper.java
│   └── UserMapper.java
│
├── model/                   # Entidades JPA
│   ├── User.java
│   ├── CapturedPokemon.java
│   ├── FavoritePokemon.java
│   ├── Team.java
│   ├── TeamMember.java
│   ├── MarketListing.java
│   ├── Role.java            # Enum: USER, ADMIN
│   ├── AuthProvider.java    # Enum: LOCAL, GOOGLE
│   └── ListingStatus.java   # Enum: ACTIVE, SOLD, CANCELLED
│
├── repository/              # Spring Data JPA repositories
│   ├── UserRepository.java
│   ├── CapturedPokemonRepository.java
│   ├── FavoritePokemonRepository.java
│   ├── TeamRepository.java
│   ├── TeamMemberRepository.java
│   └── MarketListingRepository.java
│
├── security/                # Infraestructura de autenticación
│   ├── CustomUserDetails.java
│   ├── CustomUserDetailsService.java
│   ├── JwtAuthenticationFilter.java
│   └── JwtTokenProvider.java
│
├── service/                 # Lógica de negocio
│   ├── AuthService.java
│   ├── UserService.java
│   ├── PokemonService.java
│   ├── PokeApiClient.java   # Cliente WebClient → PokéAPI v2
│   ├── CollectionService.java
│   ├── TeamService.java
│   ├── CompareService.java
│   ├── MarketService.java
│   ├── AdminService.java
│   └── SupportService.java  # Integración con Anthropic API
│
└── WildDexApplication.java  # Punto de entrada

src/main/resources/
├── application.yml          # Configuración central
└── db/migration/            # Migraciones Flyway (V1 a V5)

src/test/java/com/wilddex/service/
├── AuthServiceTest.java
├── UserServiceTest.java
├── PokemonServiceTest.java
├── PokeApiClientTest.java
├── CollectionServiceTest.java
├── TeamServiceTest.java
├── CompareServiceTest.java
├── MarketServiceTest.java
├── AdminServiceTest.java
└── SupportServiceTest.java
```

---

## 5. Modelo de Datos

### 5.1 Diagrama Entidad-Relación

```
┌──────────────────┐
│      users       │
├──────────────────┤
│ id (PK)          │
│ username (UK)    │
│ email (UK)       │
│ password         │
│ profile_image_url│
│ role             │──── USER | ADMIN
│ provider         │──── LOCAL | GOOGLE
│ provider_id      │
│ otp_code         │
│ otp_expiry       │
│ email_verified   │
│ enabled          │
│ coins            │──── Default: 1000
│ created_at       │
│ updated_at       │
└────────┬─────────┘
         │ 1
         │
    ┌────┴────┬──────────────┬───────────────┐
    │         │              │               │
    ▼ *       ▼ *            ▼ *             ▼ *
┌──────────┐ ┌──────────┐ ┌──────────┐ ┌───────────────┐
│captured_ │ │favorite_ │ │  teams   │ │market_listings│
│pokemon   │ │pokemon   │ │          │ │               │
├──────────┤ ├──────────┤ ├──────────┤ ├───────────────┤
│id (PK)   │ │id (PK)   │ │id (PK)   │ │id (PK)        │
│user_id   │ │user_id   │ │name      │ │seller_id (FK) │
│pokemon_id│ │pokemon_id│ │descriptn │ │buyer_id (FK)  │
│pokemon_  │ │pokemon_  │ │user_id   │ │pokemon_id     │
│  name    │ │  name    │ │created_at│ │pokemon_name   │
│captured_ │ │favorited_│ │updated_at│ │price          │
│  at      │ │  at      │ └─────┬────┘ │status         │
└──────────┘ └──────────┘       │ 1    │created_at     │
                                │      │sold_at        │
  UK: (user_id,   UK: (user_id, ▼ *    └───────────────┘
       pokemon_id)     pokemon_id)
                          ┌────────────┐
                          │team_members│   status:
                          ├────────────┤   ACTIVE | SOLD |
                          │id (PK)     │   CANCELLED
                          │team_id (FK)│
                          │pokemon_id  │
                          │pokemon_name│
                          │slot (1-6)  │
                          └────────────┘
                          UK: (team_id, slot)
```

### 5.2 Migraciones Flyway

| Versión | Archivo | Descripción |
|---|---|---|
| V1 | `V1__create_users.sql` | Tabla `users` con campos de autenticación, OTP y monedas |
| V2 | `V2__create_captured_pokemon.sql` | Tabla `captured_pokemon` + índice por `user_id` |
| V3 | `V3__create_favorite_pokemon.sql` | Tabla `favorite_pokemon` + índice por `user_id` |
| V4 | `V4__create_teams.sql` | Tablas `teams` y `team_members` + índice por `user_id` |
| V5 | `V5__create_market_listings.sql` | Tabla `market_listings` + índices por `status` y `seller_id` |

---

## 6. Módulos Funcionales y Endpoints

### 6.1 Autenticación (`/api/v1/auth`) — Público

| Método | Endpoint | Descripción | Req. |
|---|---|---|---|
| POST | `/register` | Registro local + envío OTP | PKX-001 |
| POST | `/login` | Login con credenciales + envío OTP | PKX-002 |
| POST | `/verify-otp` | Verificar OTP → retorna JWT | PKX-002 |
| POST | `/resend-otp` | Reenviar código OTP | PKX-002 |
| POST | `/oauth2/google` | Login/registro con Google OAuth2 | PKX-001/002 |

### 6.2 Usuarios (`/api/v1/users`) — Autenticado

| Método | Endpoint | Descripción | Req. |
|---|---|---|---|
| GET | `/me` | Obtener perfil del usuario actual | PKX-004 |
| PATCH | `/me` | Actualizar username, imagen o contraseña | PKX-004 |

### 6.3 Pokémon (`/api/v1/pokemon`) — Autenticado

| Método | Endpoint | Descripción | Req. |
|---|---|---|---|
| GET | `/` | Listado paginado | PKX-005 |
| GET | `/search?query=` | Búsqueda por nombre parcial | PKX-006 |
| GET | `/type/{type}` | Filtrar por tipo elemental | PKX-007 |
| GET | `/generation/{gen}` | Filtrar por generación | PKX-007 |
| GET | `/{idOrName}` | Detalle completo de un Pokémon | PKX-008 |
| GET | `/{id}/evolution` | Cadena evolutiva | PKX-009 |
| GET | `/{id}/stats` | Estadísticas de uso (capturas, favoritos, equipos) | — |
| GET | `/compare?pokemon1=&pokemon2=` | Comparar dos Pokémon lado a lado | PKX-013 |

### 6.4 Colección (`/api/v1/collection`) — Autenticado

| Método | Endpoint | Descripción | Req. |
|---|---|---|---|
| GET | `/captured` | Listar Pokémon capturados | PKX-012 |
| POST | `/captured` | Capturar un Pokémon | PKX-010 |
| DELETE | `/captured/{pokemonId}` | Liberar un Pokémon | PKX-010 |
| GET | `/favorites` | Listar Pokémon favoritos | PKX-012 |
| POST | `/favorites` | Agregar a favoritos | PKX-011 |
| DELETE | `/favorites/{pokemonId}` | Quitar de favoritos | PKX-011 |

### 6.5 Equipos (`/api/v1/teams`) — Autenticado

| Método | Endpoint | Descripción | Req. |
|---|---|---|---|
| GET | `/` | Listar equipos del usuario | PKX-012 |
| GET | `/{id}` | Detalle de un equipo | PKX-012 |
| POST | `/` | Crear equipo (máx. 6 miembros) | PKX-012 |
| PUT | `/{id}` | Actualizar equipo | PKX-012 |
| DELETE | `/{id}` | Eliminar equipo | PKX-012 |

### 6.6 Mercado (`/api/v1/market`) — Autenticado

| Método | Endpoint | Descripción | Req. |
|---|---|---|---|
| GET | `/` | Explorar publicaciones activas (paginado + búsqueda) | PKX-015 |
| POST | `/publish` | Publicar Pokémon capturado para venta | PKX-014 |
| POST | `/{listingId}/buy` | Comprar Pokémon con monedas | PKX-016 |
| DELETE | `/{listingId}` | Cancelar publicación propia | PKX-014 |
| GET | `/my-listings` | Mis publicaciones activas | — |
| GET | `/my-purchases` | Mi historial de compras | — |

### 6.7 Administración (`/api/v1/admin`) — ROLE_ADMIN

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/users` | Listar usuarios (paginado) |
| PATCH | `/users/{id}/toggle` | Habilitar/deshabilitar usuario |
| PATCH | `/users/{id}/role` | Cambiar rol (USER ↔ ADMIN) |
| GET | `/stats` | Estadísticas globales del sistema |

### 6.8 Soporte IA (`/api/v1/support`) — Público

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/chat` | Chat de soporte con asistente IA (Claude Haiku) |

---

## 7. Seguridad

### 7.1 Estrategia de Autenticación

El sistema implementa un flujo de autenticación en dos pasos:

1. **Registro/Login local:** el usuario envía credenciales → el sistema genera un OTP de 6 dígitos, lo almacena en la BD con expiración de 30 minutos y lo envía por email vía SMTP.
2. **Verificación OTP:** el usuario envía el código → si es válido, el sistema genera un par de tokens JWT (access: 24h, refresh: 7 días).
3. **OAuth2 Google:** flujo alternativo que emite JWT directamente sin OTP.

### 7.2 Configuración de Spring Security

- **Sesiones:** stateless (`STATELESS`) — cada request se autentica con JWT.
- **CSRF:** deshabilitado (API REST sin estado).
- **Filtro JWT:** `JwtAuthenticationFilter` se ejecuta antes de `UsernamePasswordAuthenticationFilter`.
- **Contraseñas:** cifradas con `BCryptPasswordEncoder`.
- **Autorización por roles:** `@PreAuthorize("hasRole('ADMIN')")` en el módulo de administración.

### 7.3 Endpoints Públicos vs Protegidos

| Patrón | Acceso |
|---|---|
| `/api/v1/auth/**` | Público |
| `/api/v1/support/**` | Público |
| `/swagger-ui/**`, `/v3/api-docs/**` | Público |
| `/api/v1/admin/**` | `ROLE_ADMIN` |
| Todo lo demás | Autenticado (JWT válido) |

---

## 8. Integraciones Externas

### 8.1 PokéAPI v2

- **Base URL:** `https://pokeapi.co/api/v2`
- **Cliente:** `PokeApiClient` usando `WebClient` (Spring WebFlux, no-blocking).
- **Datos consultados en vivo:** listado, búsqueda, filtros por tipo/generación, detalle, estadísticas, habilidades, descripción (español/inglés), cadena evolutiva.
- **Los datos de Pokémon NO se persisten en la BD local.** Solo se almacenan IDs y nombres en las tablas de colección/equipo/mercado como referencia.

### 8.2 Google OAuth2

- **Provider:** Google (OpenID Connect).
- **Redirect URI:** `{baseUrl}/api/v1/auth/oauth2/callback/{registrationId}`
- **Scopes:** `openid`, `profile`, `email`.
- **Resultado:** el usuario se crea/actualiza con `provider=GOOGLE` y recibe JWT sin pasar por OTP.

### 8.3 SMTP (Email/OTP)

- **Host por defecto:** `smtp.gmail.com:587` (STARTTLS).
- **Uso:** envío de códigos OTP de 6 dígitos para verificación en dos pasos.
- **Expiración OTP:** 30 minutos.

### 8.4 Anthropic API (Soporte IA)

- **Modelo:** `claude-haiku-4-5-20251001` (configurable).
- **Uso:** chat de soporte técnico accesible sin autenticación.

---

## 9. Caché

Se utiliza **Caffeine** como proveedor de caché local en memoria.

- **Configuración:** `maximumSize=500, expireAfterWrite=30m`
- **Cachés definidos en `PokeApiClient`:**

| Nombre de caché | Clave | Descripción |
|---|---|---|
| `pokemonList` | `offset-limit` | Listado paginado de Pokémon |
| `pokemonCount` | — | Total de Pokémon en PokéAPI |
| `pokemonSearch` | `query` | Resultados de búsqueda por nombre |
| `pokemonByType` | `type` | Pokémon filtrados por tipo |
| `pokemonByGeneration` | `generation` | Pokémon filtrados por generación |
| `pokemonDetail` | `idOrName` | Detalle completo de un Pokémon |
| `evolutionChain` | `pokemonId` | Cadena evolutiva |

---

## 10. Manejo de Errores

El sistema centraliza el manejo de excepciones en `GlobalExceptionHandler` usando `@ControllerAdvice`. Las excepciones personalizadas son:

| Excepción | HTTP Status | Uso |
|---|---|---|
| `BadRequestException` | 400 | Datos de entrada inválidos |
| `UnauthorizedException` | 401 | Credenciales incorrectas o token inválido |
| `ForbiddenException` | 403 | Acción no permitida para el rol actual |
| `ResourceNotFoundException` | 404 | Entidad no encontrada (usuario, Pokémon, equipo, etc.) |
| `ConflictException` | 409 | Duplicados (username, email, Pokémon ya capturado, etc.) |

Todos los endpoints responden con el wrapper genérico `ApiResponse<T>` que estandariza la estructura de respuesta.

---

## 11. Testing

- **Framework:** Spring Boot Test + Spring Security Test.
- **Cobertura:** JaCoCo con **mínimo 70%** de cobertura de líneas a nivel `BUNDLE`.
- **Exclusiones de cobertura:** clases `*Config`, `*Application`, paquetes `dto/**`, `entity/**`, `document/**`.
- **Tests unitarios por servicio:** cada servicio del sistema tiene su clase de test correspondiente (`AuthServiceTest`, `PokemonServiceTest`, `MarketServiceTest`, etc.).

---

## 12. Infraestructura

### 12.1 Docker Compose

El archivo `docker-compose.yml` define un único servicio:

- **postgres:** PostgreSQL 15 Alpine, puerto `5432`, volumen persistente `postgres_data`.
- La aplicación Spring Boot se ejecuta fuera del contenedor (en desarrollo).

### 12.2 Variables de Entorno

| Variable | Descripción | Default |
|---|---|---|
| `GOOGLE_CLIENT_ID` | Client ID de Google OAuth2 | — |
| `GOOGLE_CLIENT_SECRET` | Client Secret de Google OAuth2 | — |
| `MAIL_HOST` | Host SMTP | `smtp.gmail.com` |
| `MAIL_PORT` | Puerto SMTP | `587` |
| `MAIL_USERNAME` | Email del remitente | — |
| `MAIL_PASSWORD` | Contraseña/App Password del remitente | — |
| `JWT_SECRET` | Clave secreta para firmar tokens JWT | (dev default) |
| `ANTHROPIC_API_KEY` | API Key de Anthropic | — |
| `ANTHROPIC_MODEL` | Modelo de IA para soporte | `claude-haiku-4-5-20251001` |

---

## 13. Documentación de API

La documentación interactiva está disponible vía **Swagger UI** (SpringDoc OpenAPI):

- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

---

## 14. Trazabilidad de Requerimientos

| Requerimiento | Descripción | Controlador | Servicio |
|---|---|---|---|
| PKX-001 | Registro de usuario | AuthController | AuthService |
| PKX-002 | Inicio de sesión (OTP) | AuthController | AuthService |
| PKX-003 | Cerrar sesión | (Invalidación client-side del JWT) | — |
| PKX-004 | Gestión de perfil | UserController | UserService |
| PKX-005 | Listar Pokémon | PokemonController | PokemonService + PokeApiClient |
| PKX-006 | Buscar Pokémon | PokemonController | PokemonService + PokeApiClient |
| PKX-007 | Filtrar Pokémon | PokemonController | PokemonService + PokeApiClient |
| PKX-008 | Ver detalle de Pokémon | PokemonController | PokemonService + PokeApiClient |
| PKX-009 | Ver cadena evolutiva | PokemonController | PokemonService + PokeApiClient |
| PKX-010 | Marcar como capturado | CollectionController | CollectionService |
| PKX-011 | Marcar como favorito | CollectionController | CollectionService |
| PKX-012 | Ver colección personal | CollectionController + TeamController | CollectionService + TeamService |
| PKX-013 | Comparar Pokémon | CompareController | CompareService + PokeApiClient |
| PKX-014 | Publicar en el mercado | MarketController | MarketService |
| PKX-015 | Explorar el mercado | MarketController | MarketService |
| PKX-016/017 | Compra de Pokémon | MarketController | MarketService |
