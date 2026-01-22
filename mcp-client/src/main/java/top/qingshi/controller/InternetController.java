package top.qingshi.controller;

import top.qingshi.bean.ChatEntity;
import top.qingshi.service.ChatService;
import top.qingshi.service.SesrXngService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName HelloController
 * @Version 1.0
 * @Description HelloController
 **/
@RestController
@RequestMapping("internet")
public class InternetController
{

    @Resource
    private SesrXngService sesrXngService;

    @Resource
    private ChatService chatService;

    @GetMapping("/test")
    public Object test(@RequestParam("query") String query){
        return sesrXngService.search(query);
    }

    @PostMapping("/search")
    public void search(@RequestBody ChatEntity chatEntity, HttpServletRequest request, HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        
        // 从请求中获取用户ID（由JWT拦截器注入）
        Long userId = (Long) request.getAttribute("userId");
        chatService.doInternetSearch(chatEntity, userId);
    }

}
