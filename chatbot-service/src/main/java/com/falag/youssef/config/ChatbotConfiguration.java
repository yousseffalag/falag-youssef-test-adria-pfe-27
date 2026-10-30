package com.falag.youssef.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatbotConfiguration {

    @Bean
    ChatClient chatClient(ChatClient.Builder builder, ToolCallbackProvider mcpTools) {
        return builder.defaultTools(mcpTools).build();
    }
}
