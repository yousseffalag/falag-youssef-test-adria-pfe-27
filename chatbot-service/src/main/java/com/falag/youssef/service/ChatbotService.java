package com.falag.youssef.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ChatbotService {

    private static final String SYSTEM_PROMPT = """
            Tu es l'assistant bancaire E-Bank. Utilise les outils MCP pour consulter ou effectuer les opérations demandées.
            N'invente jamais de compte, de solde ou de transaction. N'effectue une opération qui modifie des données
            bancaires que si l'utilisateur l'a clairement demandée dans son message.
            Réponds en français de façon concise et confirme le résultat obtenu par l'outil.
            """;

    private final ChatClient chatClient;

    public ChatbotService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String chat(String message) {
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .call()
                .content();
    }
}
