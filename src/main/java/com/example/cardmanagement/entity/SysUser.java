package com.example.cardmanagement.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户实体类
 * 对应 sys_user 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_user")
public class SysUser {

    /**
     * 用户ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名
     */
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    /**
     * 姓
     */
    @Column(name = "first_name")
    private String firstName;

    /**
     * 名
     */
    @Column(name = "last_name")
    private String lastName;

    /**
     * 邮箱地址
     */
    @Column(name = "email")
    private String email;

    /**
     * 密码
     * 只参与数据库读写，不返回给前端
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * 是否启用：0禁用 1启用
     */
    @Column(name = "is_enabled", columnDefinition = "tinyint(1) default 1")
    private Boolean isEnabled;

    /**
     * 是否管理员：0否 1是
     */
    @Column(name = "is_admin", columnDefinition = "tinyint(1) default 0")
    private Boolean isAdmin;

    /**
     * 创建时间
     */
    @Column(name = "create_time", columnDefinition = "datetime default CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    /**
     * 最近登录时间
     */
    @Column(name = "last_login_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginTime;
}