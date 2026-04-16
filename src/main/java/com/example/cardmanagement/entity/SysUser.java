package com.example.cardmanagement.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.util.Date;

/**
 * 用户实体类
 * 对应sys_user表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_user")
public class SysUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username; // 用户名

    @Column(name = "first_name")
    private String firstName; // 姓

    @Column(name = "last_name")
    private String lastName; // 名

    @Column(name = "email")
    private String email; // 邮箱地址

    @Column(name = "password", nullable = false)
    private String password; // 密码

    @Column(name = "is_enabled", columnDefinition = "tinyint(1) default 1")
    private Boolean isEnabled; // 是否启用

    @Column(name = "is_admin", columnDefinition = "tinyint(1) default 0")
    private Boolean isAdmin; // 是否为管理员

    @Column(name = "create_time", columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private Date createTime; // 创建时间

    @Column(name = "last_login_time")
    private Date lastLoginTime; // 最近登录时间
}