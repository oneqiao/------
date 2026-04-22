package com.example.cardmanagement.vo;

import lombok.Data;

/**
 * 登录响应 VO。
 */
@Data
public class LoginVO {

    private String token;
    private UserVO userInfo;
}
