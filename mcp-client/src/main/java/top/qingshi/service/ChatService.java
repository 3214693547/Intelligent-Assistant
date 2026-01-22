package top.qingshi.service;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import reactor.core.publisher.Flux;
import top.qingshi.bean.ChatEntity;

import java.util.List;

public interface ChatService
{

    /**
     * @Description: 测试大模型交互聊天
     * @Author 风间影月
     * @param prompt
     * @return String
     */
    public String chatTest(String prompt);

    /**
     * @Description: 测试大模型流式交互聊天(流式响应JSON)
     * @Author 风间影月
     * @param prompt
     * @return Flux<ChatResponse>
     */
    public Flux<ChatResponse> streamResponse(String prompt);

    /**
     * @Description: 测试大模型流式交互聊天(流式响应String)
     * @Author 风间影月
     * @param prompt
     * @return Flux<ChatResponse>
     */
    public Flux<String> streamStr(String prompt);

    /**
     * @Description: 和大模型交互
     * @Author 风间影月
     * @param chatEntity
     * @param userId 用户ID
     */
    public void doChat(ChatEntity chatEntity, Long userId);

    /**
     * @Description: Rag知识库检索汇总给大模型输出
     * @Author 风间影月
     * @param chatEntity
     * @param ragContext
     * @param userId 用户ID
     */
    public void doChatRagSearch(ChatEntity chatEntity, List<Document> ragContext, Long userId);

    /**
     * @Description: 基于searxng的实时联网搜索
     * @param chatEntity
     * @param userId 用户ID
     */
    public void doInternetSearch(ChatEntity chatEntity, Long userId);
}
