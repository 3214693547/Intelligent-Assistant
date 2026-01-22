package top.qingshi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import top.qingshi.bean.ChatHistory;
import top.qingshi.mapper.ChatHistoryMapper;
import top.qingshi.service.ChatHistoryService;

import java.util.List;

/**
 * 聊天历史记录服务实现
 */
@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {

    @Resource
    private ChatHistoryMapper chatHistoryMapper;

    @Override
    public void saveHistory(ChatHistory chatHistory) {
        chatHistoryMapper.insert(chatHistory);
    }

    @Override
    public void saveHistoryBatch(List<ChatHistory> chatHistories) {
        // 使用 MyBatis-Plus 的批量插入方法
        super.saveBatch(chatHistories);
    }

    @Override
    public List<ChatHistory> getHistoryPage(Long userId, Long beforeId, Integer pageSize) {
        return chatHistoryMapper.selectPageByUserId(userId, beforeId, pageSize);
    }
}
