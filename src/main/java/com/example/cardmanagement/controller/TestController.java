package com.example.cardmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 生成BCrypt密码
     * @param password 原始密码
     * @return 加密后的密码
     */
    @GetMapping("/encode/{password}")
    public String encodePassword(@PathVariable String password) {
        String encodedPassword = passwordEncoder.encode(password);
        System.out.println("Encoded password for '" + password + "': " + encodedPassword);
        return encodedPassword;
    }

    /**
     * 验证密码
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    @GetMapping("/verify/{rawPassword}/{encodedPassword}")
    public boolean verifyPassword(@PathVariable String rawPassword, @PathVariable String encodedPassword) {
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        System.out.println("Password match: " + matches);
        return matches;
    }

    /**
     * 生成默认密码的BCrypt值
     * @return 加密后的密码
     */
    @GetMapping("/encode-default")
    public String encodeDefaultPassword() {
        String password = "123456";
        String encodedPassword = passwordEncoder.encode(password);
        System.out.println("Encoded password for '123456': " + encodedPassword);
        return encodedPassword;
    }
}