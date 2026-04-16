package com.example.cardmanagement.service;

import com.example.cardmanagement.entity.Agent;
import com.example.cardmanagement.repository.AgentRepository;
import com.example.cardmanagement.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 代理服务类
 * 处理代理相关的业务逻辑
 */
@Service
public class AgentService {
    
    @Autowired
    private AgentRepository agentRepository;
    
    @Autowired
    private MerchantRepository merchantRepository;
    
    /**
     * 分页查询代理列表
     * @param pageable 分页参数
     * @param name 代理名称
     * @param status 状态
     * @return Page<Map<String, Object>> 分页代理列表，包含商户数量
     */
    public Page<Map<String, Object>> getAgentList(Pageable pageable, String name, Integer status) {
        Specification<Agent> spec = (Root<Agent> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 代理名称搜索
            if (name != null && !name.isEmpty()) {
                predicates.add(cb.like(root.get("name"), "%" + name + "%"));
            }
            
            // 状态
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<Agent> agents = agentRepository.findAll(spec, pageable);
        
        // 转换为包含商户数量的Map
        return agents.map(agent -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", agent.getId());
            map.put("name", agent.getName());
            map.put("balance", agent.getBalance());
            map.put("status", agent.getStatus());
            map.put("totalRechargeAmount", agent.getTotalRechargeAmount());
            map.put("singleCardFee", agent.getSingleCardFee());
            map.put("rechargeRate", agent.getRechargeRate());
            map.put("createTime", agent.getCreateTime());
            
            // 计算商户数量
            long merchantCount = merchantRepository.countByAgentId(agent.getId());
            map.put("merchantCount", merchantCount);
            
            return map;
        });
    }
    
    /**
     * 新增代理
     * @param agent 代理对象
     * @return Agent 保存后的代理对象
     */
    @Transactional
    public Agent addAgent(Agent agent) {
        return agentRepository.save(agent);
    }
    
    /**
     * 编辑代理
     * @param id 代理ID
     * @param agent 代理对象
     * @return Agent 更新后的代理对象
     */
    @Transactional
    public Agent updateAgent(Long id, Agent agent) {
        // 查询代理
        Agent existingAgent = agentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("代理不存在"));
        
        // 更新代理信息
        if (agent.getName() != null) {
            existingAgent.setName(agent.getName());
        }
        if (agent.getStatus() != null) {
            existingAgent.setStatus(agent.getStatus());
        }
        if (agent.getSingleCardFee() != null) {
            existingAgent.setSingleCardFee(agent.getSingleCardFee());
        }
        if (agent.getRechargeRate() != null) {
            existingAgent.setRechargeRate(agent.getRechargeRate());
        }
        
        return agentRepository.save(existingAgent);
    }
    
    /**
     * 删除代理
     * @param id 代理ID
     */
    @Transactional
    public void deleteAgent(Long id) {
        // 检查代理是否存在
        if (!agentRepository.existsById(id)) {
            throw new IllegalArgumentException("代理不存在");
        }
        
        // 检查是否有商户关联
        long merchantCount = merchantRepository.countByAgentId(id);
        if (merchantCount > 0) {
            throw new IllegalArgumentException("该代理下存在关联商户，请先删除或转移商户");
        }
        
        agentRepository.deleteById(id);
    }
}