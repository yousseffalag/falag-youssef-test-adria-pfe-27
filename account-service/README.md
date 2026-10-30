# Account-Service (étape C.1 — cœur et REST)

Service Spring Boot qui gère les comptes et expose des APIs REST, GraphQL et MCP sur le port `8081`.
Les comptes de démonstration sont créés en base H2 mémoire au démarrage.

## Lancer le service

Depuis ce dossier :

```bash
mvn spring-boot:run
```

## Routes REST

| Méthode | Route | Description |
|---|---|---|
| `GET` | `/api/accounts` | Liste tous les comptes |
| `GET` | `/api/accounts?customerReference=CUST-001` | Filtre par client |
| `GET` | `/api/accounts/{id}` | Consulte un compte |
| `POST` | `/api/accounts` | Crée un compte |
| `PATCH` | `/api/accounts/{id}/status` | Change le statut |
| `POST` | `/api/accounts/{id}/debit` | Débite un compte (route de service interne) |
| `POST` | `/api/accounts/{id}/credit` | Crédite un compte (route de service interne) |

GraphQL est exposé sur `/graphql` (schéma dans `src/main/resources/graphql/account.graphqls`).
Le serveur MCP expose des outils de lecture et de gestion de compte via HTTP.

Les erreurs métier utilisent les réponses `ProblemDetail` : compte absent en `404`, règle métier en `422`.
Les requêtes d'exemple sont dans `rest-client-tests.http`.

L'intégration Feign/Resilience4J avec `transaction-service` reste à faire dans l'étape suivante.
