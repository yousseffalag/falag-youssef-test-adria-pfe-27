# Transaction Consumer Service

Consomme `bank.transactions.created` et journalise la clé `accountId` et le JSON reçu. Ce service ne modifie aucune
donnée bancaire.

Démarrer Discovery Service et le broker depuis la racine du projet avec `docker compose -f docker-compose.kafka.yml up -d`, puis lancer
`TransactionConsumerApplication` sur le port `8084`. Le consumer s'enregistre dans Eureka et démarre un serveur web minimal pour son enregistrement.
