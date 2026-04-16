package com.example.cardmanagement.service;

import com.example.cardmanagement.entity.SysUser;
import com.example.cardmanagement.repository.SysUserRepository;
import com.example.cardmanagement.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户服务类
 * 处理用户相关的业务逻辑
 */
@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private SysUserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 包含token和用户信息的对象
     */
    @Transactional
    public Object login(String username, String password) {
        logger.info("Login attempt for username: {}", username);
        
        // 查询用户
        SysUser user = userRepository.findByUsername(username);
        logger.info("Found user: {}", user);
        
        if (user == null) {
            logger.info("User not found: {}", username);
            throw new IllegalArgumentException("用户名或密码错误");
        }
        
        // 检查用户是否启用
        logger.info("User is enabled: {}", user.getIsEnabled());
        if (!user.getIsEnabled()) {
            logger.info("User disabled: {}", username);
            throw new IllegalArgumentException("用户已被禁用");
        }
        
        // 验证密码
        boolean passwordMatch = passwordEncoder.matches(password, user.getPassword());
        logger.info("Password match: {}", passwordMatch);
        if (!passwordMatch) {
            logger.info("Password mismatch for user: {}", username);
            throw new IllegalArgumentException("用户名或密码错误");
        }
        
        // 更新最近登录时间
        user.setLastLoginTime(new Date());
        userRepository.save(user);
        
        // 生成token
        String token = jwtUtil.generateToken(username);
        
        // 构建返回对象
        logger.info("Login successful for user: {}", username);
        return new LoginResponse(token, user);
    }
    
    /**
     * 获取当前用户信息
     * @param username 用户名
     * @return SysUser 用户对象
     */
    public SysUser getCurrentUser(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * 分页查询用户列表
     * @param pageable 分页参数
     * @param keyword 关键词
     * @param isEnabled 是否启用
     * @param isAdmin 是否管理员
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return Page<SysUser> 分页用户列表
     */
    public Page<SysUser> getUserList(Pageable pageable, String keyword, Integer isEnabled, Integer isAdmin, Date startTime, Date endTime) {
        Specification<SysUser> spec = (Root<SysUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 关键词搜索
            if (keyword != null && !keyword.isEmpty()) {
                predicates.add(cb.or(
                    cb.like(root.get("username"), "%" + keyword + "%"),
                    cb.like(root.get("firstName"), "%" + keyword + "%"),
                    cb.like(root.get("lastName"), "%" + keyword + "%"),
                    cb.like(root.get("email"), "%" + keyword + "%")
                ));
            }
            
            // 是否启用
            if (isEnabled != null) {
                predicates.add(cb.equal(root.get("isEnabled"), isEnabled == 1));
            }
            
            // 是否管理员
            if (isAdmin != null) {
                predicates.add(cb.equal(root.get("isAdmin"), isAdmin == 1));
            }
            
            // 开始时间
            if (startTime != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createTime"), startTime));
            }
            
            // 结束时间
            if (endTime != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createTime"), endTime));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return userRepository.findAll(spec, pageable);
    }
    
    /**
     * 添加用户
     * @param user 用户对象
     * @return SysUser 保存后的用户对象
     */
    @Transactional
    public SysUser addUser(SysUser user) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 设置创建时间
        user.setCreateTime(new Date());
        
        return userRepository.save(user);
    }
    
    /**
     * 编辑用户
     * @param id 用户ID
     * @param user 用户对象
     * @return SysUser 更新后的用户对象
     */
    @Transactional
    public SysUser updateUser(Long id, SysUser user) {
        // 查询用户
        SysUser existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        // 更新用户信息
        if (user.getFirstName() != null) {
            existingUser.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null) {
            existingUser.setLastName(user.getLastName());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getIsEnabled() != null) {
            existingUser.setIsEnabled(user.getIsEnabled());
        }
        if (user.getIsAdmin() != null) {
            existingUser.setIsAdmin(user.getIsAdmin());
        }
        
        return userRepository.save(existingUser);
    }
    
    /**
     * 删除用户
     * @param id 用户ID
     */
    @Transactional
    public void deleteUser(Long id) {
        // 检查用户是否存在
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        userRepository.deleteById(id);
    }
    
    /**
     * 登录响应内部类
     */
    public class LoginResponse {
        private String token;
        private SysUser userInfo;
        
        public LoginResponse(String token, SysUser userInfo) {
            this.token = token;
            this.userInfo = userInfo;
        }
        
        public String getToken() {
            return token;
        }
        
        public SysUser getUserInfo() {
            return userInfo;
        }
    }
}