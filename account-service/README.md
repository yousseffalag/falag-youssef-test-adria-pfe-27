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

Les requêtes d'exemple sont dans `rest-client-tests.http`.

Le service reste volontairement limité à la gestion des comptes. Les réponses d'erreur utilisent `ResponseStatusException`
de Spring, sans exceptions métier personnalisées ni handler global.

La console H2 est disponible à l'adresse `http://localhost:8081/h2-console`.
Dans l'écran de connexion, utiliser l'URL JDBC `jdbc:h2:mem:account-db;DB_CLOSE_DELAY=-1`, l'utilisateur `sa` et un mot de passe vide.
