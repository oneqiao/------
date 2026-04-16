package com.example.cardmanagement.repository;

import com.example.cardmanagement.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 商户数据访问层
 * 继承JpaRepository实现基本CRUD操作
 * 继承JpaSpecificationExecutor实现复杂查询
 */
public interface MerchantRepository extends JpaRepository<Merchant, Long>, JpaSpecificationExecutor<Merchant> {
    
    /**
     * 根据商户号查询商户
     * @param merchantNo 商户号
     * @return Merchant 商户对象
     */
    Merchant findByMerchantNo(String merchantNo);
    
    /**
     * 根据登录账号查询商户
     * @param loginAccount 登录账号
     * @return Merchant 商户对象
     */
    Merchant findByLoginAccount(String loginAccount);
    
    /**
     * 根据代理ID查询商户数量
     * @param agentId 代理ID
     * @return 商户数量
     */
    long countByAgentId(Long agentId);
}