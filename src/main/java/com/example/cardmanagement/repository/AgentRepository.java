package com.example.cardmanagement.repository;

import com.example.cardmanagement.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 代理数据访问层
 * 继承JpaRepository实现基本CRUD操作
 * 继承JpaSpecificationExecutor实现复杂查询
 */
public interface AgentRepository extends JpaRepository<Agent, Long>, JpaSpecificationExecutor<Agent> {
}