package top.qingshi.service;

import top.qingshi.bean.ChatHistory;

import java.util.List;

/**
 * 聊天历史记录服务接口
 */
public interface ChatHistoryService {

    /**
     * 保存聊天记录
     * @param chatHistory 聊天记录
     */
    void saveHistory(ChatHistory chatHistory);

    /**
     * 批量保存聊天记录
     * @param chatHistories 聊天记录列表
     */
    void saveHistoryBatch(List<ChatHistory> chatHistories);

    /**
     * 分页查询用户的聊天历史记录
     * @param userId 用户ID
     * @param beforeId 查询此ID之前的记录（用于分页，首次传null）
     * @param pageSize 每页数量
     * @return 聊天记录列表（按时间倒序）
     */
    List<ChatHistory> getHistoryPage(Long userId, Long beforeId, Integer pageSize);
}
