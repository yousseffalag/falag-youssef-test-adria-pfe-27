# Transaction-Service (Partie C)

Service Spring Boot sur le port `8082` pour les paiements et virements. Il expose REST, GraphQL et MCP,
persiste les transactions dans H2 et appelle `account-service` via OpenFeign.

OpenFeign appelle `account-service`; Resilience4J ouvre le circuit en cas de panne. Les refus métier restent `404` ou
`422`, tandis qu'une indisponibilité renvoie `503`. Le plafond local est `1000`.

## Lancer

Démarrer `account-service`, puis depuis ce dossier :

```bash
mvn spring-boot:run
```

Les requêtes d'exemple figurent dans `rest-client-tests.http`. REST est sous `/api`, GraphQL sous `/graphql` et les
outils MCP sont publiés par le serveur MCP du service.

