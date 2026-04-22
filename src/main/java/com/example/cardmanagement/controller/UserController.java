package com.example.cardmanagement.controller;

import com.example.cardmanagement.dto.SysUserDTO;
import com.example.cardmanagement.service.UserService;
import com.example.cardmanagement.util.PageUtil;
import com.example.cardmanagement.util.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 用户控制器。
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 分页查询用户列表。
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
            return Response.success(PageUtil.buildPageResponse(users));
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            return Response.error(500, "查询用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 添加用户。
     */
    @PostMapping
    public Response addUser(@RequestBody SysUserDTO userDTO) {
        if (userDTO == null) {
            return Response.error(400, "请求体不能为空");
        }

        try {
            return Response.success("添加用户成功", userService.addUser(userDTO));
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            return Response.error(500, "添加用户失败: " + e.getMessage());
        }
    }

    /**
     * 编辑用户。
     */
    @PutMapping("/{id}")
    public Response updateUser(@PathVariable Long id, @RequestBody SysUserDTO userDTO) {
        if (userDTO == null) {
            return Response.error(400, "请求体不能为空");
        }

        try {
            return Response.success("编辑用户成功", userService.updateUser(id, userDTO));
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            return Response.error(500, "编辑用户失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户。
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
