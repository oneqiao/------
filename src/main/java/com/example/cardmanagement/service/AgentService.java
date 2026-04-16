package com.example.cardmanagement.service;

import com.example.cardmanagement.entity.Agent;
import com.example.cardmanagement.repository.AgentRepository;
import com.example.cardmanagement.repository.MerchantRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代理服务类
 * 处理代理相关的业务逻辑
 */
@Service
public class AgentService {

    private final AgentRepository agentRepository;
    private final MerchantRepository merchantRepository;

    public AgentService(AgentRepository agentRepository, MerchantRepository merchantRepository) {
        this.agentRepository = agentRepository;
        this.merchantRepository = merchantRepository;
    }

    /**
     * 分页查询代理列表
     */
    @Transactional(readOnly = true)
    @NonNull
    public Page<Map<String, Object>> getAgentList(@NonNull Pageable pageable, String name, Integer status) {
        Specification<Agent> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.trim().isEmpty()) {
                predicates.add(cb.like(root.get("name"), "%" + name.trim() + "%"));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Agent> agents = agentRepository.findAll(spec, pageable);

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
            map.put("merchantCount", merchantRepository.countByAgentId(agent.getId()));
            return map;
        });
    }

    /**
     * 新增代理
     */
    @Transactional
    @NonNull
    public Agent addAgent(@NonNull Agent agent) {
        if (agent.getName() == null || agent.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("代理名称不能为空");
        }

        if (agent.getBalance() == null) {
            agent.setBalance(BigDecimal.ZERO);
        }
        if (agent.getStatus() == null) {
            agent.setStatus(1);
        }
        if (agent.getTotalRechargeAmount() == null) {
            agent.setTotalRechargeAmount(BigDecimal.ZERO);
        }
        if (agent.getSingleCardFee() == null) {
            agent.setSingleCardFee(BigDecimal.ZERO);
        }
        if (agent.getRechargeRate() == null) {
            agent.setRechargeRate(BigDecimal.ZERO);
        }

        return agentRepository.save(agent);
    }

    /**
     * 编辑代理
     */
    @Transactional
    @NonNull
    public Agent updateAgent(@NonNull Long id, @NonNull Agent agent) {
        Agent existingAgent = agentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("代理不存在"));

        if (agent.getName() != null && !agent.getName().trim().isEmpty()) {
            existingAgent.setName(agent.getName().trim());
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
     */
    @Transactional
    public void deleteAgent(@NonNull Long id) {
        if (!agentRepository.existsById(id)) {
            throw new IllegalArgumentException("代理不存在");
        }

        long merchantCount = merchantRepository.countByAgentId(id);
        if (merchantCount > 0) {
            throw new IllegalArgumentException("该代理下存在关联商户，请先删除或转移商户");
        }

        agentRepository.deleteById(id);
    }
}