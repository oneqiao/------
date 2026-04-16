package com.example.cardmanagement.vo;

import com.example.cardmanagement.entity.SysUser;
import lombok.Data;

/**
 * 登录VO类
 * 用于登录响应的数据传输对象
 */
@Data
public class LoginVO {

    private String token; // 登录 token
    private SysUser userInfo; // 用户信息
}