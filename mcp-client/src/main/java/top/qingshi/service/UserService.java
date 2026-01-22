package top.qingshi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.qingshi.bean.LoginRequest;
import top.qingshi.bean.LoginResponse;
import top.qingshi.bean.RegisterRequest;
import top.qingshi.bean.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    void register(RegisterRequest request);

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);

    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);
}
