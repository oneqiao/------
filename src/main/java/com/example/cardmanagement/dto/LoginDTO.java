package com.example.cardmanagement.dto;

import lombok.Data;

/**
 * 登录数据传输对象
 * 用于接收用户登录时的用户名和密码
 */
@Data
public class LoginDTO {

    private String username; // 用户名
    private String password; // 密码
}