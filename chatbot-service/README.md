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

Démarrer Discovery Service, Account Service et Transaction Service, puis lancer `ChatbotServiceApplication` sur le port `8083`. Le Config Service doit aussi être démarré avant Transaction Service.

Exemples de requêtes REST : `chatbot-client.http`.
