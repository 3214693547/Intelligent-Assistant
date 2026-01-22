package top.qingshi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.qingshi.bean.ChatHistory;

import java.util.List;

/**
 * 聊天历史记录 Mapper
 */
public interface ChatHistoryMapper extends BaseMapper<ChatHistory> {

    /**
     * 分页查询用户的聊天历史记录（按时间倒序）
     * @param userId 用户ID
     * @param beforeId 查询此ID之前的记录（用于分页）
     * @param pageSize 每页数量
     * @return 聊天记录列表
     */
    @Select("<script>" +
            "SELECT * FROM chat_history " +
            "WHERE user_id = #{userId} " +
            "<if test='beforeId != null'>" +
            "AND id &lt; #{beforeId} " +
            "</if>" +
            "ORDER BY id DESC " +
            "LIMIT #{pageSize}" +
            "</script>")
    List<ChatHistory> selectPageByUserId(@Param("userId") Long userId, 
                                          @Param("beforeId") Long beforeId, 
                                          @Param("pageSize") Integer pageSize);
}
