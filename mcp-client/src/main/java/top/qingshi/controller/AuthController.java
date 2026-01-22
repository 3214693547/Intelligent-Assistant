package top.qingshi.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.qingshi.bean.LoginRequest;
import top.qingshi.bean.LoginResponse;
import top.qingshi.bean.RegisterRequest;
import top.qingshi.service.UserService;
import top.qingshi.utils.LeeResult;
import top.qingshi.utils.SSEServer;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public LeeResult register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return LeeResult.ok("注册成功");
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public LeeResult login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return LeeResult.ok(response);
    }

    /**
     * 用户注销
     */
    @PostMapping("/logout")
    public LeeResult logout(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");
        
        log.info("用户 {} (用户ID: {}) 注销登录", username, userId);
        
        // 清除 SSE 连接
        SSEServer.remove(String.valueOf(userId));
        log.info("已清除用户 {} 的 SSE 连接", userId);
        
        return LeeResult.ok("注销成功");
    }
}
