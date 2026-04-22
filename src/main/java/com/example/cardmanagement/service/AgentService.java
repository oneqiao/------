package com.example.cardmanagement.service;

import com.example.cardmanagement.dto.AgentDTO;
import com.example.cardmanagement.entity.Agent;
import com.example.cardmanagement.repository.AgentRepository;
import com.example.cardmanagement.repository.MerchantRepository;
import com.example.cardmanagement.vo.AgentVO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 代理服务类。
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
     * 分页查询代理列表。
     */
    @Transactional(readOnly = true)
    @NonNull
    public Page<AgentVO> getAgentList(@NonNull Pageable pageable, String name, Integer status) {
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

        return agentRepository.findAll(spec, pageable).map(this::toAgentVO);
    }

    /**
     * 新增代理。
     */
    @Transactional
    @NonNull
    public AgentVO addAgent(@NonNull AgentDTO agentDTO) {
        if (agentDTO.getName() == null || agentDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("代理名称不能为空");
        }

        Agent agent = new Agent();
        agent.setName(agentDTO.getName().trim());
        agent.setBalance(agentDTO.getBalance() != null ? agentDTO.getBalance() : BigDecimal.ZERO);
        agent.setStatus(agentDTO.getStatus() != null ? agentDTO.getStatus() : 1);
        agent.setTotalRechargeAmount(
                agentDTO.getTotalRechargeAmount() != null ? agentDTO.getTotalRechargeAmount() : BigDecimal.ZERO
        );
        agent.setSingleCardFee(agentDTO.getSingleCardFee() != null ? agentDTO.getSingleCardFee() : BigDecimal.ZERO);
        agent.setRechargeRate(agentDTO.getRechargeRate() != null ? agentDTO.getRechargeRate() : BigDecimal.ZERO);

        return toAgentVO(agentRepository.save(agent));
    }

    /**
     * 编辑代理。
     */
    @Transactional
    @NonNull
    public AgentVO updateAgent(@NonNull Long id, @NonNull AgentDTO agentDTO) {
        Agent existingAgent = agentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("代理不存在"));

        if (agentDTO.getName() != null && !agentDTO.getName().trim().isEmpty()) {
            existingAgent.setName(agentDTO.getName().trim());
        }
        if (agentDTO.getStatus() != null) {
            existingAgent.setStatus(agentDTO.getStatus());
        }
        if (agentDTO.getSingleCardFee() != null) {
            existingAgent.setSingleCardFee(agentDTO.getSingleCardFee());
        }
        if (agentDTO.getRechargeRate() != null) {
            existingAgent.setRechargeRate(agentDTO.getRechargeRate());
        }
        if (agentDTO.getBalance() != null) {
            existingAgent.setBalance(agentDTO.getBalance());
        }
        if (agentDTO.getTotalRechargeAmount() != null) {
            existingAgent.setTotalRechargeAmount(agentDTO.getTotalRechargeAmount());
        }

        return toAgentVO(agentRepository.save(existingAgent));
    }

    /**
     * 删除代理。
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

    private AgentVO toAgentVO(Agent agent) {
        AgentVO agentVO = new AgentVO();
        agentVO.setId(agent.getId());
        agentVO.setName(agent.getName());
        agentVO.setBalance(agent.getBalance());
        agentVO.setStatus(agent.getStatus());
        agentVO.setTotalRechargeAmount(agent.getTotalRechargeAmount());
        agentVO.setSingleCardFee(agent.getSingleCardFee());
        agentVO.setRechargeRate(agent.getRechargeRate());
        agentVO.setCreateTime(agent.getCreateTime());
        agentVO.setMerchantCount(merchantRepository.countByAgentId(agent.getId()));
        return agentVO;
    }
}
