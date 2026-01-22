package top.qingshi.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.qingshi.bean.ChatEntity;
import top.qingshi.service.ChatService;

/**
 * @ClassName HelloController
 * @Author 风间影月
 * @Version 1.0
 * @Description HelloController
 **/
@RestController
@RequestMapping("chat")
public class ChatController
{


    @Resource
    private ChatService chatService;

    @PostMapping("doChat")
    public void doChat(@RequestBody ChatEntity chatEntity, HttpServletRequest request){
        // 从请求中获取用户ID（由JWT拦截器注入）
        Long userId = (Long) request.getAttribute("userId");
        chatService.doChat(chatEntity, userId);
    }

}
