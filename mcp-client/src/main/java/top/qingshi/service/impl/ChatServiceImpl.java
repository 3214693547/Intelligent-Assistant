package top.qingshi.service.impl;

import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import top.qingshi.bean.ChatEntity;
import top.qingshi.bean.ChatHistory;
import top.qingshi.bean.ChatResponseEntity;
import top.qingshi.bean.SearchResult;
import top.qingshi.enums.SSEMsgType;
import top.qingshi.service.ChatHistoryService;
import top.qingshi.service.ChatService;
import top.qingshi.service.SesrXngService;
import top.qingshi.utils.SSEServer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService
{
    @Resource
    private ChatClient chatClient;

    @Resource
    private SesrXngService sesrXngService;
    
    @Resource
    private ChatHistoryService chatHistoryService;

    //private ChatMemory chatMemory;

    private String systemPrompt =
                                """
                                    你是一个非常聪明的人工智能助手，可以帮我解决很多问题，我为你取一个名字，你的名字叫‘LaGoGo’。
                                """;

//        // 构造器注入，自动配置方式（推荐）
//    public ChatServiceImpl(ChatClient.Builder chatClientBuilder) {
//
//        this.chatClient = chatClientBuilder
////                .defaultSystem(systemPrompt)
//                .build()
//        ;
//    }

    /**
     * 提示词的三大类型
     *  1. system
     *  2. user
     *  3. assistant
     */

    // 构造器注入，自动配置方式（推荐）
//    public ChatServiceImpl(ChatClient.Builder chatClientBuilder, ToolCallbackProvider tools, ChatMemory chatMemory) {
//
//        this.chatClient = chatClientBuilder
//                .defaultToolCallbacks(tools)
//                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
////                .defaultSystem(systemPrompt)
//                .build()
//        ;
//    }

    @Override
    public String chatTest(String prompt) {
//        try {
//            Thread.sleep(6000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        return chatClient.prompt(prompt).call().content();
    }

    @Override
    public Flux<ChatResponse> streamResponse(String prompt) {
//        try {
//            Thread.sleep(6000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        return chatClient.prompt(prompt).stream().chatResponse();
    }

    @Override
    public Flux<String> streamStr(String prompt) {
//        try {
//            Thread.sleep(6000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        return chatClient.prompt(prompt).stream().content();
    }

    @Override
    public void doChat(ChatEntity chatEntity, Long userId) {

        String userIdStr = chatEntity.getCurrentUserName();
        String prompt = chatEntity.getMessage();
        String botMsgId = chatEntity.getBotMsgId();
        
        // 保存用户消息
        ChatHistory userMessage = new ChatHistory();
        userMessage.setUserId(userId);
        userMessage.setRole("user");
        userMessage.setContent(prompt);
        userMessage.setCreatedAt(LocalDateTime.now());
        chatHistoryService.saveHistory(userMessage);

        Flux<String> stringFlux = chatClient
                .prompt(prompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userId))
                .stream()
                .content();

        List<String> list = stringFlux.toStream().map(chatResponse -> {
            String content = chatResponse.toString();
            SSEServer.sendMsg(userIdStr, content, SSEMsgType.ADD);
            log.info("content: {}", content);
            return content;
        }).collect(Collectors.toList());
        //可保存，根据需要
        String fullContent = list.stream().collect(Collectors.joining());
        
        // 保存 AI 回复
        ChatHistory assistantMessage = new ChatHistory();
        assistantMessage.setUserId(userId);
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(fullContent);
        assistantMessage.setCreatedAt(LocalDateTime.now());
        chatHistoryService.saveHistory(assistantMessage);

        ChatResponseEntity chatResponseEntity = new ChatResponseEntity(fullContent, botMsgId);

        SSEServer.sendMsg(userIdStr, JSONUtil.toJsonStr(chatResponseEntity), SSEMsgType.FINISH);

    }

    // Dify 智能体引擎构建平台

    private static final String ragPROMPT = """
                                              基于上下文的知识库内容回答问题：
                                              【上下文】
                                              {context}

                                              【问题】
                                              {question}

                                              【输出】
                                              如果没有查到，请回复：不知道。
                                              如果查到，请回复具体的内容。不相关的近似内容不必提到。
                                              请直接输出答案，不要添加任何引用标记、来源编号或链接。
                                              """;

    @Override
    public void doChatRagSearch(ChatEntity chatEntity, List<Document> ragContext, Long userId) {

        String userIdStr = chatEntity.getCurrentUserName();
        String question = chatEntity.getMessage();
        String botMsgId = chatEntity.getBotMsgId();
        
        // 保存用户消息
        ChatHistory userMessage = new ChatHistory();
        userMessage.setUserId(userId);
        userMessage.setRole("user");
        userMessage.setContent(question);
        userMessage.setCreatedAt(LocalDateTime.now());
        chatHistoryService.saveHistory(userMessage);

        // 构建提示词
        String context = null;
        if (ragContext != null && ragContext.size() > 0) {
            context = ragContext.stream()
                    .map(Document::getText)
                    .collect(Collectors.joining("\n"));
        }

        // 组装提示词
        Prompt prompt = new Prompt(ragPROMPT
                .replace("{context}", context)
                .replace("{question}", question));

        System.out.println(prompt.toString());

        Flux<String> stringFlux = chatClient
                .prompt(prompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userId))
                .stream()
                .content();

        List<String> list = stringFlux.toStream().map(chatResponse -> {
            String content = chatResponse.toString();
            SSEServer.sendMsg(userIdStr, content, SSEMsgType.ADD);
            log.info("content: {}", content);
            return content;
        }).collect(Collectors.toList());

        String fullContent = list.stream().collect(Collectors.joining());
        
        // 保存 AI 回复
        ChatHistory assistantMessage = new ChatHistory();
        assistantMessage.setUserId(userId);
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(fullContent);
        assistantMessage.setCreatedAt(LocalDateTime.now());
        chatHistoryService.saveHistory(assistantMessage);

        ChatResponseEntity chatResponseEntity = new ChatResponseEntity(fullContent, botMsgId);

        SSEServer.sendMsg(userIdStr, JSONUtil.toJsonStr(chatResponseEntity), SSEMsgType.FINISH);

    }

    private static final String sesrXngPROMPT = """
                                              你是一个互联网搜索大师，请基于以下互联网返回的结果作为上下文，根据你的理解结合用户的提问综合后，生成并且输出专业的回答：
                                              【上下文】
                                              {context}

                                              【问题】
                                              {question}

                                              【输出】
                                              如果没有查到，请回复：不知道。
                                              如果查到，请回复具体的内容。
                                              """;

    @Override
    public void doInternetSearch(ChatEntity chatEntity, Long userId) {

        String userIdStr = chatEntity.getCurrentUserName();
        String question = chatEntity.getMessage();
        String botMsgId = chatEntity.getBotMsgId();
        
        // 保存用户消息
        ChatHistory userMessage = new ChatHistory();
        userMessage.setUserId(userId);
        userMessage.setRole("user");
        userMessage.setContent(question);
        userMessage.setCreatedAt(LocalDateTime.now());
        chatHistoryService.saveHistory(userMessage);

        List<SearchResult> searchResults = sesrXngService.search(question);

        String finalPrompt = buildSesrXngPrompt(question, searchResults);

        // 组装提示词
        Prompt prompt = new Prompt(finalPrompt);

        System.out.println(prompt.toString());

        Flux<String> stringFlux = chatClient
                .prompt(prompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userId))
                .stream()
                .content();

        List<String> list = stringFlux.toStream().map(chatResponse -> {
            String content = chatResponse.toString();
            SSEServer.sendMsg(userIdStr, content, SSEMsgType.ADD);
            log.info("content: {}", content);
            return content;
        }).collect(Collectors.toList());

        String fullContent = list.stream().collect(Collectors.joining());
        
        // 保存 AI 回复
        ChatHistory assistantMessage = new ChatHistory();
        assistantMessage.setUserId(userId);
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(fullContent);
        assistantMessage.setCreatedAt(LocalDateTime.now());
        chatHistoryService.saveHistory(assistantMessage);

        ChatResponseEntity chatResponseEntity = new ChatResponseEntity(fullContent, botMsgId);

        SSEServer.sendMsg(userIdStr, JSONUtil.toJsonStr(chatResponseEntity), SSEMsgType.FINISH);
    }

    private static String buildSesrXngPrompt(String question, List<SearchResult> searchResults) {

        StringBuilder context = new StringBuilder();

        searchResults.forEach(searchResult -> {
            context.append(
                    String.format("<context>\n[来源] %s \n [摘要] %s \n </context>\n",
                            searchResult.getUrl(),
                            searchResult.getContent()));
        });

        return sesrXngPROMPT
                .replace("{context}", context)
                .replace("{question}", question);
    }
}
