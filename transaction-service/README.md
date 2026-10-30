# Transaction-Service (Partie C)

Service Spring Boot sur le port `8082` pour les paiements et virements. Il expose REST, GraphQL et MCP,
persiste les transactions dans H2 et appelle `account-service` via OpenFeign.

Le circuit breaker Resilience4J protège les appels Feign. Une indisponibilité de `account-service` renvoie HTTP `503`;
les refus métier `404`/`422` sont conservés et ignorés dans le calcul du circuit. Le plafond de démonstration vaut
`1000` et sera externalisé via Config-Service dans la Partie D.

## Lancer

Démarrer `account-service`, puis depuis ce dossier :

```bash
mvn spring-boot:run
```

Les requêtes d'exemple figurent dans `rest-client-tests.http`. REST est sous `/api`, GraphQL sous `/graphql` et les
outils MCP sont publiés par le serveur MCP du service.

## Limite de cohérence

Les deux services ne partagent pas de transaction distribuée. Le virement compense au mieux un échec du crédit cible;
une panne pendant la compensation peut nécessiter une réconciliation manuelle. Une saga persistée serait l'étape de
durcissement adaptée.
