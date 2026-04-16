package com.example.cardmanagement.dto;

import lombok.Data;

/**
 * 用户DTO类
 * 用于接收用户相关数据的传输对象
 */
@Data
public class SysUserDTO {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Boolean isEnabled;
    private Boolean isAdmin;
}