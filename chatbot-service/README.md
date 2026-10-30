# Chatbot service

Le chatbot peut utiliser Gemini ou l'API compatible OpenAI de Mistral, avec les outils MCP d'Account Service et de Transaction Service.

Choisir le fournisseur avec `LLM_PROVIDER` (`google-genai` par défaut pour Gemini, ou `openai` pour Mistral). Définir les variables nécessaires dans PowerShell avant le démarrage. Exemple Gemini :

```powershell
$env:LLM_PROVIDER="google-genai"
$env:GEMINI_API_KEY="votre-cle-gemini"
$env:GEMINI_MODEL="gemini-3.5-flash-lite"
```

Exemple Mistral :

```powershell
$env:LLM_PROVIDER="openai"
$env:MISTRAL_API_KEY="votre-cle-mistral"
$env:MISTRAL_BASE_URL="https://api.mistral.ai/v1"
$env:MISTRAL_MODEL="mistral-small-latest"
```

Les clés restent dans les variables d'environnement et ne doivent pas être ajoutées aux fichiers du projet.

Si le service est lancé depuis IntelliJ IDEA, ouvrir **Run > Edit Configurations**, sélectionner `ChatbotServiceApplication`, puis ajouter les variables dans **Environment variables** (et non dans les options JVM). Pour Gemini, renseigner `LLM_PROVIDER=google-genai`, `GEMINI_API_KEY=<nouvelle-clé>` et `GEMINI_MODEL=gemini-3.5-flash-lite`. Pour Mistral, renseigner `LLM_PROVIDER=openai`, `MISTRAL_API_KEY=<nouvelle-clé>`, `MISTRAL_BASE_URL=https://api.mistral.ai/v1` et `MISTRAL_MODEL=mistral-small-latest`. Redémarrer ensuite l'application.

Démarrer Discovery Service et Config Service, puis Account Service et Transaction Service. Lancer ensuite `ChatbotServiceApplication` sur le port `8083`. Le chatbot contacte directement les serveurs MCP aux URL `ACCOUNT_MCP_URL` (par défaut `http://localhost:8081`) et `TRANSACTION_MCP_URL` (par défaut `http://localhost:8082`) ; s'ils ne sont pas démarrés ou si ces URL ne sont pas accessibles, le chatbot échoue au démarrage avec `Connection refused`.

Exemples de requêtes REST : `chatbot-client.http`.
