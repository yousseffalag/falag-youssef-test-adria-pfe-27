# Chatbot service

Le chatbot utilise Gemini et les outils MCP d'Account Service et de Transaction Service.

Définir la clé Gemini dans PowerShell avant le démarrage :

```powershell
$env:GEMINI_API_KEY="votre-cle-gemini"
```

Démarrer Discovery Service, Account Service et Transaction Service, puis lancer `ChatbotServiceApplication` sur le port `8083`. Le Config Service doit aussi être démarré avant Transaction Service.

Exemples de requêtes REST : `chatbot-client.http`.
