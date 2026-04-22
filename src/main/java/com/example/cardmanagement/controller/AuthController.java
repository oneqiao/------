package com.example.cardmanagement.controller;

import com.example.cardmanagement.dto.LoginDTO;
import com.example.cardmanagement.entity.SysUser;
import com.example.cardmanagement.service.UserService;
import com.example.cardmanagement.util.Response;
import com.example.cardmanagement.vo.LoginVO;
import com.example.cardmanagement.vo.UserVO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户登录。
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
            LoginVO result = userService.login(username, password);
            return Response.success("登录成功", result);
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            return Response.error(500, "登录失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户信息。
     */
    @GetMapping("/current")
    public Response getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || authentication.getPrincipal() == null) {
                return Response.error(401, "未登录");
            }

            Object principal = authentication.getPrincipal();
            if (!(principal instanceof SysUser sysUser)) {
                return Response.error(401, "未登录");
            }

            UserVO currentUser = userService.getCurrentUser(sysUser.getUsername());
            if (currentUser == null) {
                return Response.error(401, "未登录");
            }

            return Response.success(currentUser);
        } catch (Exception e) {
            return Response.error(401, "未登录");
        }
    }

    /**
     * 用户登出。
     */
    @PostMapping("/logout")
    public Response logout() {
        SecurityContextHolder.clearContext();
        return Response.success("登出成功");
    }
}
