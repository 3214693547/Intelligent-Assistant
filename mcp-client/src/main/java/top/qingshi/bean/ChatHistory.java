package top.qingshi.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天历史记录实体
 */
@Data
@TableName("chat_history")
public class ChatHistory {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 会话ID（可选）
     */
    private String sessionId;

    /**
     * 角色：user/assistant/system
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 扩展信息（JSON格式）
     */
    private String metadata;
}
