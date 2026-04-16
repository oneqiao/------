package com.example.cardmanagement.controller;

import com.example.cardmanagement.entity.SysUser;
import com.example.cardmanagement.service.UserService;
import com.example.cardmanagement.util.PageUtil;
import com.example.cardmanagement.util.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 用户控制器
 * 处理用户管理相关的接口
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 分页查询用户列表
     */
    @GetMapping
    public Response getUserList(@RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(required = false) String keyword,
                                @RequestParam(required = false) Integer isEnabled,
                                @RequestParam(required = false) Integer isAdmin,
                                @RequestParam(required = false)
                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
                                @RequestParam(required = false)
                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        try {
            Pageable pageable = PageUtil.createPageable(page, size);
            var users = userService.getUserList(pageable, keyword, isEnabled, isAdmin, startTime, endTime);
            var response = PageUtil.buildPageResponse(users);
            return Response.success(response);
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            return Response.error(500, "查询用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 添加用户
     */
    @PostMapping
    public Response addUser(@RequestBody SysUser user) {
        if (user == null) {
            return Response.error(400, "请求体不能为空");
        }

        try {
            SysUser savedUser = userService.addUser(user);
            return Response.success("添加用户成功", savedUser);
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            return Response.error(500, "添加用户失败: " + e.getMessage());
        }
    }

    /**
     * 编辑用户
     */
    @PutMapping("/{id}")
    public Response updateUser(@PathVariable Long id, @RequestBody SysUser user) {
        if (user == null) {
            return Response.error(400, "请求体不能为空");
        }

        try {
            // 调用业务层的更新方法
            SysUser updatedUser = userService.updateUser(id, user);
            return Response.success("编辑用户成功", updatedUser);
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());  // 捕获业务异常
        } catch (Exception e) {
            // 打印错误信息帮助追踪问题
            e.printStackTrace();
            return Response.error(500, "编辑用户失败: " + e.getMessage());  // 捕获其他异常
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Response deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return Response.success("删除用户成功");
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            return Response.error(500, "删除用户失败: " + e.getMessage());
        }
    }
}