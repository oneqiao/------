package com.example.cardmanagement.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import jakarta.annotation.PostConstruct;

/**
 * JWT工具类
 * 用于生成和验证JWT token
 */
@Component
public class JwtUtil {

    /**
     * JWT密钥（从配置文件获取，Base64 编码）
     */
    @Value("${jwt.secret}")
    private String secret;  // 密钥（Base64 编码）

    /**
     * JWT过期时间（毫秒）
     */
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * JWT 签名密钥
     */
    private SecretKey secretKey;

    /**
     * 初始化方法，加载并解码密钥
     */
    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);  // 解码 Base64 密钥
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);  // 生成 HMAC 密钥
    }

    /**
     * 生成 JWT token
     * 
     * @param username 用户名
     * @return 生成的 JWT token
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);

        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiration);  // 设置过期时间

        // 使用密钥生成 JWT
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)  // 设置用户名作为主题
                .setIssuedAt(now)  // 设置发布时间
                .setExpiration(expireDate)  // 设置过期时间
                .signWith(secretKey, SignatureAlgorithm.HS256)  // 使用 HMAC-SHA256 签名
                .compact();  // 返回生成的 token
    }

    /**
     * 解析 JWT token
     * 
     * @param token JWT token
     * @return JWT 解析出的 Claims 信息
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)  // 设置签名密钥
                .build()
                .parseClaimsJws(token)  // 解析 JWT
                .getBody();  // 获取 body 部分（包含载荷）
    }

    /**
     * 从 JWT token 中获取用户名
     * 
     * @param token JWT token
     * @return 从 token 中解析出的用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);  // 获取用户名
    }

    /**
     * 验证 JWT 是否有效
     * 
     * @param token JWT token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().after(new Date());  // 判断是否过期
        } catch (Exception e) {
            return false;  // 如果解析时发生异常，认为 token 无效
        }
    }
}