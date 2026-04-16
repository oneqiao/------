package com.example.cardmanagement.controller;

import com.example.cardmanagement.entity.SysUser;
import com.example.cardmanagement.service.UserService;
import com.example.cardmanagement.util.PageUtil;
import com.example.cardmanagement.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    @Autowired
    private UserService userService;
    
    /**
     * 分页查询用户列表
     * @param page 页码
     * @param size 每页条数
     * @param keyword 关键词
     * @param isEnabled 是否启用
     * @param isAdmin 是否管理员
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return Response 用户列表
     */
    @GetMapping
    public Response getUserList(@RequestParam(defaultValue = "1") int page, 
                              @RequestParam(defaultValue = "10") int size, 
                              @RequestParam(required = false) String keyword, 
                              @RequestParam(required = false) Integer isEnabled, 
                              @RequestParam(required = false) Integer isAdmin, 
                              @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime, 
                              @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        try {
            // 创建分页请求
            Pageable pageable = PageUtil.createPageable(page, size);
            
            // 查询用户列表
            var users = userService.getUserList(pageable, keyword, isEnabled, isAdmin, startTime, endTime);
            
            // 构建分页响应
            var response = PageUtil.buildPageResponse(users);
            return Response.success(response);
        } catch (Exception e) {
            return Response.error(500, "查询用户列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 添加用户
     * @param user 用户对象
     * @return Response 添加结果
     */
    @PostMapping
    public Response addUser(@RequestBody SysUser user) {
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
     * @param id 用户ID
     * @param user 用户对象
     * @return Response 编辑结果
     */
    @PutMapping("/{id}")
    public Response updateUser(@PathVariable Long id, @RequestBody SysUser user) {
        try {
            SysUser updatedUser = userService.updateUser(id, user);
            return Response.success("编辑用户成功", updatedUser);
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            return Response.error(500, "编辑用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return Response 删除结果
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