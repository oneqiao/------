package com.example.cardmanagement.service;

import com.example.cardmanagement.entity.SysUser;
import com.example.cardmanagement.repository.SysUserRepository;
import com.example.cardmanagement.util.JwtUtil;
import com.example.cardmanagement.vo.LoginVO;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户服务类
 * 处理用户相关的业务逻辑
 */
@Service
@Transactional(readOnly = true)
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final SysUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(SysUserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 包含 token 和用户信息的对象
     */
    @Transactional
    @NonNull
    public LoginVO login(@NonNull String username, @NonNull String password) {
        String trimUsername = username.trim();
        String trimPassword = password.trim();

        logger.info("Login attempt for username: {}", trimUsername);

        if (trimUsername.isEmpty() || trimPassword.isEmpty()) {
            throw new IllegalArgumentException("用户名和密码不能为空");
        }

        SysUser user = userRepository.findByUsername(trimUsername);
        if (user == null) {
            logger.warn("User not found: {}", trimUsername);
            throw new IllegalArgumentException("用户名或密码错误");
        }

        if (Boolean.FALSE.equals(user.getIsEnabled())) {
            logger.warn("User disabled: {}", trimUsername);
            throw new IllegalArgumentException("用户已被禁用");
        }

        boolean passwordMatch = passwordEncoder.matches(trimPassword, user.getPassword());
        if (!passwordMatch) {
            logger.warn("Password mismatch for user: {}", trimUsername);
            throw new IllegalArgumentException("用户名或密码错误");
        }

        user.setLastLoginTime(new Date());
        userRepository.save(user);

        String token = jwtUtil.generateToken(trimUsername);

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserInfo(user);

        logger.info("Login successful for user: {}", trimUsername);
        return loginVO;
    }

    /**
     * 获取当前用户信息
     *
     * @param username 用户名
     * @return 用户对象
     */
    public SysUser getCurrentUser(@NonNull String username) {
        return userRepository.findByUsername(username.trim());
    }

    /**
     * 分页查询用户列表
     *
     * @param pageable  分页参数
     * @param keyword   关键词
     * @param isEnabled 是否启用
     * @param isAdmin   是否管理员
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 分页用户列表
     */
    @NonNull
    public Page<SysUser> getUserList(@NonNull Pageable pageable,
                                     String keyword,
                                     Integer isEnabled,
                                     Integer isAdmin,
                                     Date startTime,
                                     Date endTime) {
        Specification<SysUser> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                String likeKeyword = "%" + keyword.trim() + "%";
                predicates.add(cb.or(
                        cb.like(root.get("username"), likeKeyword),
                        cb.like(root.get("firstName"), likeKeyword),
                        cb.like(root.get("lastName"), likeKeyword),
                        cb.like(root.get("email"), likeKeyword)
                ));
            }

            if (isEnabled != null) {
                predicates.add(cb.equal(root.get("isEnabled"), isEnabled == 1));
            }

            if (isAdmin != null) {
                predicates.add(cb.equal(root.get("isAdmin"), isAdmin == 1));
            }

            if (startTime != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createTime"), startTime));
            }

            if (endTime != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createTime"), endTime));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return userRepository.findAll(spec, pageable);
    }

    /**
     * 添加用户
     *
     * @param user 用户对象
     * @return 保存后的用户对象
     */
    @Transactional
    @NonNull
    public SysUser addUser(@NonNull SysUser user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        String username = user.getUsername().trim();

        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }

        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(user.getPassword().trim()));

        if (user.getIsEnabled() == null) {
            user.setIsEnabled(true);
        }
        if (user.getIsAdmin() == null) {
            user.setIsAdmin(false);
        }
        if (user.getCreateTime() == null) {
            user.setCreateTime(new Date());
        }

        return userRepository.save(user);
    }

    /**
     * 编辑用户
     *
     * @param id   用户ID
     * @param user 用户对象
     * @return 更新后的用户对象
     */
    @Transactional
    @NonNull
    public SysUser updateUser(@NonNull Long id, @NonNull SysUser user) {
        SysUser existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            String newUsername = user.getUsername().trim();
            SysUser sameUser = userRepository.findByUsername(newUsername);
            if (sameUser != null && !sameUser.getId().equals(existingUser.getId())) {
                throw new IllegalArgumentException("用户名已存在");
            }
            existingUser.setUsername(newUsername);
        }

        if (user.getFirstName() != null) {
            existingUser.setFirstName(user.getFirstName().trim());
        }
        if (user.getLastName() != null) {
            existingUser.setLastName(user.getLastName().trim());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail().trim());
        }
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword().trim()));
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
     *
     * @param id 用户ID
     */
    @Transactional
    public void deleteUser(@NonNull Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("用户不存在");
        }

        userRepository.deleteById(id);
    }
}