package com.example.cardmanagement.controller;

import com.example.cardmanagement.entity.SysUser;
import com.example.cardmanagement.service.UserService;
import com.example.cardmanagement.util.Response;
import com.example.cardmanagement.vo.LoginVO;
import com.example.cardmanagement.dto.LoginDTO; // 添加 LoginDTO 导入
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理用户登录、获取当前用户信息和退出登录的接口
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    // 构造器注入 UserService
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户登录
     *
     * @param loginDTO 登录数据
     * @return 登录响应
     */
    @PostMapping("/login")
    public Response login(@RequestBody LoginDTO loginDTO) {
        if (loginDTO == null) {
            return Response.error(400, "请求体不能为空");
        }

        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            return Response.error(400, "用户名和密码不能为空");
        }

        try {
            LoginVO result = userService.login(username, password); // 调用 UserService 处理登录
            return Response.success("登录成功", result);
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            return Response.error(500, "登录失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     *
     * @return 当前用户信息
     */
    @GetMapping("/current")
    public Response getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // 确保身份验证存在
            if (authentication == null || authentication.getPrincipal() == null) {
                return Response.error(401, "未登录");
            }

            Object principal = authentication.getPrincipal();
            if (!(principal instanceof SysUser)) {
                return Response.error(401, "未登录");
            }

            SysUser user = (SysUser) principal; // 获取当前用户
            return Response.success(user); // 返回用户信息
        } catch (Exception e) {
            return Response.error(401, "未登录");
        }
    }

    /**
     * 用户登出
     *
     * @return 登出响应
     */
    @PostMapping("/logout")
    public Response logout() {
        SecurityContextHolder.clearContext(); // 清除上下文中的用户信息
        return Response.success("登出成功");
    }
}