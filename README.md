# Gestion-Utilisateur — User & Role Service

Manages **users** and **roles** for Money Minder MS, integrated with **Keycloak**
for identity (registration and user synchronization).

## Tech stack
Java · Spring Boot · Spring Data JPA · MySQL · Spring Cloud (Eureka, Config, OpenFeign) · Keycloak · springdoc-openapi · Actuator

## API — port `8088`
| Method | Path | Action |
| --- | --- | --- |
| GET/POST/PUT/DELETE | `/api/utilisateurs/...` | User CRUD |
| GET/POST/PUT/DELETE | `/api/roles/...` | Role CRUD |
| POST | `/api/utilisateurs/register-keycloak` | Register a user in Keycloak |
| POST | `/api/utilisateurs/sync-keycloak` | Sync users with Keycloak |

## Data
MySQL `gestion_utilisateur` (port 3307).

## Configuration
Keycloak settings are already read from environment variables with local defaults
(`KEYCLOAK_URL`, `KEYCLOAK_REALM`, `KEYCLOAK_ADMIN_USERNAME`, `KEYCLOAK_ADMIN_PASSWORD`,
`KEYCLOAK_DEFAULT_ROLE`). Override the database credentials the same way for shared environments.

## Run
```bash
mvn spring-boot:run
```

## Part of Money Minder MS

One service of the **Money Minder MS** Spring Cloud platform. Full architecture and
the other services: [API-Gateway README](https://github.com/Money-Minder-MS/API-Gateway).
Start EurekaServer (8761) and Config-Server (8099) first.

