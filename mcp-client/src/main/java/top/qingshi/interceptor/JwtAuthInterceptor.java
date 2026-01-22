package top.qingshi.interceptor;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import top.qingshi.exception.AuthException;
import top.qingshi.utils.JwtUtil;

/**
 * JWT 认证拦截器
 */
@Slf4j
@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    @Resource
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        
        // 从请求头获取 Token
        String token = request.getHeader("Authorization");
        
        if (StringUtils.isBlank(token)) {
            throw new AuthException("未登录，请先登录");
        }

        // 去掉 Bearer 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证 Token
        if (!jwtUtil.validateToken(token)) {
            throw new AuthException("Token 无效或已过期");
        }

        // 提取用户信息并存入 request
        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);

        log.info("用户 {} 访问接口: {}", username, request.getRequestURI());

        return true;
    }
}
