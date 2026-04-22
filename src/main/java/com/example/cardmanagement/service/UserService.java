package com.example.cardmanagement.service;

import com.example.cardmanagement.dto.SysUserDTO;
import com.example.cardmanagement.entity.SysUser;
import com.example.cardmanagement.repository.SysUserRepository;
import com.example.cardmanagement.util.JwtUtil;
import com.example.cardmanagement.vo.LoginVO;
import com.example.cardmanagement.vo.UserVO;
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
 * 用户服务类。
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
     * 用户登录。
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

        if (!passwordEncoder.matches(trimPassword, user.getPassword())) {
            logger.warn("Password mismatch for user: {}", trimUsername);
            throw new IllegalArgumentException("用户名或密码错误");
        }

        user.setLastLoginTime(new Date());
        userRepository.save(user);

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(jwtUtil.generateToken(trimUsername));
        loginVO.setUserInfo(toUserVO(user));

        logger.info("Login successful for user: {}", trimUsername);
        return loginVO;
    }

    /**
     * 获取当前用户信息。
     */
    public UserVO getCurrentUser(@NonNull String username) {
        SysUser user = userRepository.findByUsername(username.trim());
        return user == null ? null : toUserVO(user);
    }

    /**
     * 分页查询用户列表。
     */
    @NonNull
    public Page<UserVO> getUserList(@NonNull Pageable pageable,
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

        return userRepository.findAll(spec, pageable).map(this::toUserVO);
    }

    /**
     * 添加用户。
     */
    @Transactional
    @NonNull
    public UserVO addUser(@NonNull SysUserDTO userDTO) {
        if (userDTO.getUsername() == null || userDTO.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }

        if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        String username = userDTO.getUsername().trim();
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setFirstName(trimToNull(userDTO.getFirstName()));
        user.setLastName(trimToNull(userDTO.getLastName()));
        user.setEmail(trimToNull(userDTO.getEmail()));
        user.setPassword(passwordEncoder.encode(userDTO.getPassword().trim()));
        user.setIsEnabled(userDTO.getIsEnabled() != null ? userDTO.getIsEnabled() : true);
        user.setIsAdmin(userDTO.getIsAdmin() != null ? userDTO.getIsAdmin() : false);
        user.setCreateTime(new Date());

        return toUserVO(userRepository.save(user));
    }

    /**
     * 编辑用户。
     */
    @Transactional
    @NonNull
    public UserVO updateUser(@NonNull Long id, @NonNull SysUserDTO userDTO) {
        SysUser existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        if (userDTO.getUsername() != null && !userDTO.getUsername().trim().isEmpty()) {
            String newUsername = userDTO.getUsername().trim();
            SysUser sameUser = userRepository.findByUsername(newUsername);
            if (sameUser != null && !sameUser.getId().equals(existingUser.getId())) {
                throw new IllegalArgumentException("用户名已存在");
            }
            existingUser.setUsername(newUsername);
        }

        if (userDTO.getFirstName() != null) {
            existingUser.setFirstName(trimToNull(userDTO.getFirstName()));
        }
        if (userDTO.getLastName() != null) {
            existingUser.setLastName(trimToNull(userDTO.getLastName()));
        }
        if (userDTO.getEmail() != null) {
            existingUser.setEmail(trimToNull(userDTO.getEmail()));
        }
        if (userDTO.getPassword() != null && !userDTO.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword().trim()));
        }
        if (userDTO.getIsEnabled() != null) {
            existingUser.setIsEnabled(userDTO.getIsEnabled());
        }
        if (userDTO.getIsAdmin() != null) {
            existingUser.setIsAdmin(userDTO.getIsAdmin());
        }

        return toUserVO(userRepository.save(existingUser));
    }

    /**
     * 删除用户。
     */
    @Transactional
    public void deleteUser(@NonNull Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("用户不存在");
        }

        userRepository.deleteById(id);
    }

    private UserVO toUserVO(SysUser user) {
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setFirstName(user.getFirstName());
        userVO.setLastName(user.getLastName());
        userVO.setEmail(user.getEmail());
        userVO.setIsEnabled(user.getIsEnabled());
        userVO.setIsAdmin(user.getIsAdmin());
        userVO.setCreateTime(user.getCreateTime());
        userVO.setLastLoginTime(user.getLastLoginTime());
        return userVO;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
