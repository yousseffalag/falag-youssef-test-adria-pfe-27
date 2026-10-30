# Chatbot service

Le chatbot utilise OpenAI GPT et les outils MCP d'Account Service et de Transaction Service.

Définir la clé API et le modèle dans PowerShell avant le démarrage :

```powershell
$env:OPENAI_API_KEY="votre-cle-openai"
$env:OPENAI_MODEL="gpt-4o-mini"
```

Les clés restent dans les variables d'environnement et ne doivent pas être ajoutées aux fichiers du projet.

Si le service est lancé depuis IntelliJ IDEA, ouvrir **Run > Edit Configurations**, sélectionner `ChatbotServiceApplication`, puis ajouter `OPENAI_API_KEY=<nouvelle-clé>` et `OPENAI_MODEL=gpt-4o-mini` dans **Environment variables** (et non dans les options JVM). Redémarrer ensuite l'application.

Démarrer Discovery Service et Config Service, puis Account Service et Transaction Service. Lancer ensuite `ChatbotServiceApplication` sur le port `8083`. Le chatbot contacte directement les serveurs MCP aux URL `ACCOUNT_MCP_URL` (par défaut `http://localhost:8081`) et `TRANSACTION_MCP_URL` (par défaut `http://localhost:8082`) ; s'ils ne sont pas démarrés ou si ces URL ne sont pas accessibles, le chatbot échoue au démarrage avec `Connection refused`.

Exemples de requêtes REST : `chatbot-client.http`.
