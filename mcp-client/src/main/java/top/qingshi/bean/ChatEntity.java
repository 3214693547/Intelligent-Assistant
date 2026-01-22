package top.qingshi.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChatEntity
{

    private String currentUserName;
    private String message;
    private String botMsgId;//对应前端，消息替换，唯一标识

}
