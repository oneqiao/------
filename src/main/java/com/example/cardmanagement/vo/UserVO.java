package com.example.cardmanagement.vo;

import lombok.Data;

import java.util.Date;

/**
 * 用户响应 VO。
 */
@Data
public class UserVO {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean isEnabled;
    private Boolean isAdmin;
    private Date createTime;
    private Date lastLoginTime;
}
