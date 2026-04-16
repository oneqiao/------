package com.example.cardmanagement.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "123456";
        String encodedPassword = encoder.encode(password);
        System.out.println("Encoded password: " + encodedPassword);
        
        // 验证数据库中的密码
        String dbPassword = "$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW";
        boolean matches = encoder.matches(password, dbPassword);
        System.out.println("Password matches: " + matches);
    }
}