package top.qingshi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import top.qingshi.bean.LoginRequest;
import top.qingshi.bean.LoginResponse;
import top.qingshi.bean.RegisterRequest;
import top.qingshi.bean.User;
import top.qingshi.mapper.UserMapper;
import top.qingshi.service.UserService;
import top.qingshi.utils.JwtUtil;
import top.qingshi.utils.PasswordUtil;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private JwtUtil jwtUtil;

    @Override
    public void register(RegisterRequest request) {
        // 检查用户名是否已存在
        User existUser = getByUsername(request.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtil.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setStatus(1);

        save(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        User user = getByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 验证密码
        if (!PasswordUtil.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查状态
        if (user.getStatus() != 1) {
            throw new RuntimeException("用户已被禁用");
        }

        // 生成 Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        return new LoginResponse(token, user.getId(), user.getUsername(), user.getNickname());
    }

    @Override
    public User getByUsername(String username) {
        return lambdaQuery()
                .eq(User::getUsername, username)
                .one();
    }
}
