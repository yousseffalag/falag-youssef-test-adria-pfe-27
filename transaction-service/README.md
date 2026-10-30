# Transaction-Service (Partie C)

Service Spring Boot sur le port `8082` pour les paiements et virements. Il expose REST, GraphQL et MCP,
persiste les transactions dans H2 et appelle `account-service` via OpenFeign. Deux transactions de démonstration sont
créées au démarrage si la base est vide.

`feign/AccountClient` appelle `account-service` avec OpenFeign. Chaque méthode porte une annotation
`@CircuitBreaker` Resilience4J et un fallback simple qui renvoie une erreur HTTP appropriée sans inventer de données.
Le plafond local est `1000`.

## Lancer

Démarrer `account-service`, puis depuis ce dossier :

```bash
mvn spring-boot:run
```

Les requêtes d'exemple figurent dans `rest-client-tests.http`. REST est sous `/api`, GraphQL sous `/graphql` et les
outils MCP sont publiés par le serveur MCP du service.

La console H2 est accessible à `http://localhost:8082/h2-console`. Utiliser l'URL JDBC
`jdbc:h2:mem:transactiondb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`, l'utilisateur `sa` et un mot de passe vide.

