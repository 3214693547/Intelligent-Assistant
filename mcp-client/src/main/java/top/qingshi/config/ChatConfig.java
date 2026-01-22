package top.qingshi.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    @Bean
    public ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
        // 创建 ChatMemory，设置每个会话最多保留20条消息
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(10)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel, ToolCallbackProvider tools,
                                 ChatMemory chatMemory) {

//                this.chatClient = chatClientBuilder
//                .defaultToolCallbacks(tools)
//                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
////                .defaultSystem(systemPrompt)
//                .build()
//        ;

        // 创建 ChatClient，并集成聊天记忆顾问
        return ChatClient.builder(chatModel)
                .defaultToolCallbacks(tools)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }
}