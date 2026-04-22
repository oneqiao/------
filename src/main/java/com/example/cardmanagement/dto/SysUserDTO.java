package com.example.cardmanagement.dto;

import lombok.Data;

/**
 * 用户请求 DTO。
 */
@Data
public class SysUserDTO {

    /**
     * 用户名。
     */
    private String username;

    /**
     * 姓。
     */
    private String firstName;

    /**
     * 名。
     */
    private String lastName;

    /**
     * 邮箱地址。
     */
    private String email;

    /**
     * 密码。
     */
    private String password;

    /**
     * 是否启用。
     */
    private Boolean isEnabled;

    /**
     * 是否管理员。
     */
    private Boolean isAdmin;
}
