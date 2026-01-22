package top.qingshi.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import top.qingshi.bean.ChatHistory;
import top.qingshi.service.ChatHistoryService;
import top.qingshi.utils.LeeResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 聊天历史记录控制器
 */
@RestController
@RequestMapping("/chat/history")
public class ChatHistoryController {

    @Resource
    private ChatHistoryService chatHistoryService;

    /**
     * 分页查询聊天历史记录
     * @param request HTTP请求（从中获取userId）
     * @param beforeId 查询此ID之前的记录（首次传null）
     * @param pageSize 每页数量（默认20）
     * @return 聊天记录列表
     */
    @GetMapping("/list")
    public LeeResult getHistory(HttpServletRequest request,
                                @RequestParam(required = false) Long beforeId,
                                @RequestParam(defaultValue = "20") Integer pageSize) {
        // 从请求中获取用户ID（由JWT拦截器注入）
        Long userId = (Long) request.getAttribute("userId");
        
        // 查询聊天历史
        List<ChatHistory> historyList = chatHistoryService.getHistoryPage(userId, beforeId, pageSize);
        
        // 判断是否还有更多数据
        boolean hasMore = historyList.size() >= pageSize;
        
        // 构造返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", historyList);
        result.put("hasMore", hasMore);
        
        return LeeResult.ok(result);
    }
}
