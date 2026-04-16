package com.example.cardmanagement.repository;

import com.example.cardmanagement.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 用户数据访问层
 * 继承JpaRepository实现基本CRUD操作
 * 继承JpaSpecificationExecutor实现复杂查询
 */
public interface SysUserRepository extends JpaRepository<SysUser, Long>, JpaSpecificationExecutor<SysUser> {
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return SysUser 用户对象
     */
    SysUser findByUsername(String username);
}