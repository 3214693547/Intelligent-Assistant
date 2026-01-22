package top.qingshi.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.qingshi.interceptor.JwtAuthInterceptor;

/**
 * Web MVC 配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private JwtAuthInterceptor jwtAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor)
                // 拦截所有接口
                .addPathPatterns("/**")
                // 排除不需要认证的接口
                .excludePathPatterns(
                        "/auth/login",      // 登录
                        "/auth/register",   // 注册
                        "/sse/**",          // SSE 连接
                        "/error"            // 错误页面
                );
    }
}
