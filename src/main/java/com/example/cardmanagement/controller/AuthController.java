package com.example.cardmanagement.controller;

import com.example.cardmanagement.entity.SysUser;
import com.example.cardmanagement.service.UserService;
import com.example.cardmanagement.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 * 处理用户登录、获取当前用户信息和退出登录的接口
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户登录
     * @param loginData 登录数据，包含username和password
     * @return Response 登录响应，包含token和用户信息
     */
    @PostMapping("/login")
    public Response login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        
        if (username == null || password == null) {
            return Response.error(400, "用户名和密码不能为空");
        }
        
        try {
            Object result = userService.login(username, password);
            return Response.success("登录成功", result);
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            return Response.error(500, "登录失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取当前用户信息
     * @return Response 当前用户信息
     */
    @GetMapping("/current")
    public Response getCurrentUser() {
        try {
            // 从Security上下文获取当前用户
            SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return Response.success(user);
        } catch (Exception e) {
            return Response.error(401, "未登录");
        }
    }
    
    /**
     * 用户登出
     * @return Response 登出响应
     */
    @PostMapping("/logout")
    public Response logout() {
        // 清除Security上下文
        SecurityContextHolder.clearContext();
        return Response.success("登出成功");
    }
}