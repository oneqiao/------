package com.example.cardmanagement.config;

import com.example.cardmanagement.entity.SysUser;
import com.example.cardmanagement.repository.SysUserRepository;
import com.example.cardmanagement.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT过滤器
 * 用于验证token并设置用户信息到Spring Security上下文中
 */
@Component
public class JwtFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private SysUserRepository userRepository;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 从请求头中获取token
        String token = request.getHeader("Authorization");
        
        // 处理token格式
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            
            // 验证token
            if (jwtUtil.validateToken(token)) {
                // 从token中获取用户名
                String username = jwtUtil.getUsernameFromToken(token);
                
                // 查询用户信息
                SysUser user = userRepository.findByUsername(username);
                
                if (user != null && user.getIsEnabled()) {
                    // 创建认证对象
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            user, null, null
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // 设置认证信息到Security上下文
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        
        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }
}