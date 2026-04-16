package com.example.cardmanagement.service;

import com.example.cardmanagement.entity.Merchant;
import com.example.cardmanagement.entity.MerchantCard;
import com.example.cardmanagement.repository.MerchantRepository;
import com.example.cardmanagement.repository.MerchantCardRepository;
import com.example.cardmanagement.util.ExportUtil;
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
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 商户服务类
 * 处理商户相关的业务逻辑
 */
@Service
public class MerchantService {
    
    @Autowired
    private MerchantRepository merchantRepository;
    
    @Autowired
    private MerchantCardRepository merchantCardRepository;
    
    /**
     * 分页查询商户列表
     * @param pageable 分页参数
     * @param merchantNo 商户号
     * @param name 商户名称
     * @param merchantType 商户类型
     * @param agentId 代理ID
     * @param accountStatus 账号状态
     * @param fundFreeze 资金冻结
     * @param negativeBalance 余额负数
     * @return Page<Map<String, Object>> 分页商户列表
     */
    public Page<Map<String, Object>> getMerchantList(Pageable pageable, String merchantNo, String name, String merchantType, 
                                                    Long agentId, Integer accountStatus, Integer fundFreeze, Integer negativeBalance) {
        Specification<Merchant> spec = (Root<Merchant> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 商户号
            if (merchantNo != null && !merchantNo.isEmpty()) {
                predicates.add(cb.equal(root.get("merchantNo"), merchantNo));
            }
            
            // 商户名称
            if (name != null && !name.isEmpty()) {
                predicates.add(cb.like(root.get("name"), "%" + name + "%"));
            }
            
            // 商户类型
            if (merchantType != null && !merchantType.isEmpty()) {
                predicates.add(cb.equal(root.get("merchantType"), merchantType));
            }
            
            // 代理ID
            if (agentId != null) {
                predicates.add(cb.equal(root.get("agentId"), agentId));
            }
            
            // 账号状态
            if (accountStatus != null) {
                predicates.add(cb.equal(root.get("accountStatus"), accountStatus));
            }
            
            // 资金冻结
            if (fundFreeze != null) {
                predicates.add(cb.equal(root.get("fundFreeze"), fundFreeze == 1));
            }
            
            // 余额负数
            if (negativeBalance != null && negativeBalance == 1) {
                predicates.add(cb.lessThan(root.get("currentBalance"), BigDecimal.ZERO));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<Merchant> merchants = merchantRepository.findAll(spec, pageable);
        
        // 转换为包含代理名称和账号状态文本的Map
        return merchants.map(merchant -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", merchant.getId());
            map.put("merchantNo", merchant.getMerchantNo());
            map.put("name", merchant.getName());
            map.put("merchantType", merchant.getMerchantType());
            map.put("agentId", merchant.getAgentId());
            map.put("agentName", merchant.getAgent() != null ? merchant.getAgent().getName() : "");
            map.put("loginAccount", merchant.getLoginAccount());
            map.put("accountStatus", merchant.getAccountStatus());
            
            // 账号状态文本
            String accountStatusText = "";
            if (merchant.getAccountStatus() != null) {
                switch (merchant.getAccountStatus()) {
                    case 0: accountStatusText = "禁用";
                        break;
                    case 1: accountStatusText = "正常";
                        break;
                    case 2: accountStatusText = "删除";
                        break;
                }
            }
            map.put("accountStatusText", accountStatusText);
            
            map.put("fundFreeze", merchant.getFundFreeze());
            map.put("currentBalance", merchant.getCurrentBalance());
            map.put("cardCount", merchant.getCardCount());
            map.put("cardBalance", merchant.getCardBalance());
            map.put("createTime", merchant.getCreateTime());
            
            return map;
        });
    }
    
    /**
     * 获取商户详情
     * @param id 商户ID
     * @return Map<String, Object> 商户详情，包含卡片列表
     */
    public Map<String, Object> getMerchantDetail(Long id) {
        // 查询商户
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商户不存在"));
        
        // 构建返回对象
        Map<String, Object> map = new HashMap<>();
        map.put("id", merchant.getId());
        map.put("merchantNo", merchant.getMerchantNo());
        map.put("name", merchant.getName());
        map.put("merchantType", merchant.getMerchantType());
        map.put("agentId", merchant.getAgentId());
        map.put("agentName", merchant.getAgent() != null ? merchant.getAgent().getName() : "");
        map.put("loginAccount", merchant.getLoginAccount());
        map.put("accountStatus", merchant.getAccountStatus());
        map.put("fundFreeze", merchant.getFundFreeze());
        map.put("currentBalance", merchant.getCurrentBalance());
        map.put("cardCount", merchant.getCardCount());
        map.put("cardBalance", merchant.getCardBalance());
        map.put("createTime", merchant.getCreateTime());
        
        // 查询卡片列表
        List<MerchantCard> cards = merchantCardRepository.findByMerchantId(id);
        map.put("cardList", cards);
        
        return map;
    }
    
    /**
     * 新增商户
     * @param merchant 商户对象
     * @return Merchant 保存后的商户对象
     */
    @Transactional
    public Merchant addMerchant(Merchant merchant) {
        // 检查商户号是否已存在
        if (merchantRepository.findByMerchantNo(merchant.getMerchantNo()) != null) {
            throw new IllegalArgumentException("商户号已存在");
        }
        
        // 检查登录账号是否已存在
        if (merchant.getLoginAccount() != null && !merchant.getLoginAccount().isEmpty()) {
            if (merchantRepository.findByLoginAccount(merchant.getLoginAccount()) != null) {
                throw new IllegalArgumentException("登录账号已存在");
            }
        }
        
        // 设置默认值
        if (merchant.getAccountStatus() == null) {
            merchant.setAccountStatus(1); // 正常
        }
        if (merchant.getFundFreeze() == null) {
            merchant.setFundFreeze(false); // 未冻结
        }
        if (merchant.getCurrentBalance() == null) {
            merchant.setCurrentBalance(BigDecimal.ZERO);
        }
        if (merchant.getCardCount() == null) {
            merchant.setCardCount(0);
        }
        if (merchant.getCardBalance() == null) {
            merchant.setCardBalance(BigDecimal.ZERO);
        }
        
        return merchantRepository.save(merchant);
    }
    
    /**
     * 编辑商户
     * @param id 商户ID
     * @param merchant 商户对象
     * @return Merchant 更新后的商户对象
     */
    @Transactional
    public Merchant updateMerchant(Long id, Merchant merchant) {
        // 查询商户
        Merchant existingMerchant = merchantRepository.findById(id).orElse(null);
        if (existingMerchant == null) {
            throw new IllegalArgumentException("商户不存在");
        }
        
        // 更新商户信息
        if (merchant.getName() != null) {
            existingMerchant.setName(merchant.getName());
        }
        if (merchant.getMerchantType() != null) {
            existingMerchant.setMerchantType(merchant.getMerchantType());
        }
        if (merchant.getAgentId() != null) {
            existingMerchant.setAgentId(merchant.getAgentId());
        }
        if (merchant.getAccountStatus() != null) {
            existingMerchant.setAccountStatus(merchant.getAccountStatus());
        }
        if (merchant.getFundFreeze() != null) {
            existingMerchant.setFundFreeze(merchant.getFundFreeze());
        }
        
        return merchantRepository.save(existingMerchant);
    }
    
    /**
     * 软删除商户
     * @param id 商户ID
     */
    @Transactional
    public void deleteMerchant(Long id) {
        // 查询商户
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商户不存在"));
        
        // 软删除：更新账号状态为2
        merchant.setAccountStatus(2);
        merchantRepository.save(merchant);
    }
    
    /**
     * 调整商户余额
     * @param id 商户ID
     * @param type 操作类型：recharge（充值）、withdraw（提现）
     * @param amount 调整金额
     * @param reason 调整原因
     * @return Map<String, Object> 调整结果，包含原余额、新余额、调整金额
     */
    @Transactional
    public Map<String, Object> adjustBalance(Long id, String type, BigDecimal amount, String reason) {
        // 查询商户
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商户不存在"));
        
        // 检查资金是否冻结
        if (merchant.getFundFreeze() != null && merchant.getFundFreeze()) {
            throw new IllegalArgumentException("商户资金已冻结，无法调整余额");
        }
        
        // 检查金额
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("调整金额必须大于0");
        }
        
        // 检查操作类型
        if (!"recharge".equals(type) && !"withdraw".equals(type)) {
            throw new IllegalArgumentException("操作类型无效");
        }
        
        // 获取原余额
        BigDecimal oldBalance = merchant.getCurrentBalance() != null ? merchant.getCurrentBalance() : BigDecimal.ZERO;
        BigDecimal newBalance;
        
        // 执行余额调整
        if ("recharge".equals(type)) {
            // 充值：增加余额
            newBalance = oldBalance.add(amount);
        } else {
            // 提现：减少余额
            if (oldBalance.compareTo(amount) < 0) {
                throw new IllegalArgumentException("余额不足");
            }
            newBalance = oldBalance.subtract(amount);
        }
        
        // 更新余额
        merchant.setCurrentBalance(newBalance);
        merchantRepository.save(merchant);
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("oldBalance", oldBalance);
        result.put("newBalance", newBalance);
        result.put("changeAmount", amount);
        
        return result;
    }
    
    /**
     * 导出商户列表为Excel
     * @param merchantNo 商户号
     * @param name 商户名称
     * @param merchantType 商户类型
     * @param agentId 代理ID
     * @param accountStatus 账号状态
     * @param fundFreeze 资金冻结
     * @param negativeBalance 余额负数
     * @return byte[] Excel文件字节数组
     * @throws IOException IO异常
     */
    public byte[] exportMerchants(String merchantNo, String name, String merchantType, Long agentId, 
                                 Integer accountStatus, Integer fundFreeze, Integer negativeBalance) throws IOException {
        // 构建查询条件
        Specification<Merchant> spec = (Root<Merchant> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 商户号
            if (merchantNo != null && !merchantNo.isEmpty()) {
                predicates.add(cb.equal(root.get("merchantNo"), merchantNo));
            }
            
            // 商户名称
            if (name != null && !name.isEmpty()) {
                predicates.add(cb.like(root.get("name"), "%" + name + "%"));
            }
            
            // 商户类型
            if (merchantType != null && !merchantType.isEmpty()) {
                predicates.add(cb.equal(root.get("merchantType"), merchantType));
            }
            
            // 代理ID
            if (agentId != null) {
                predicates.add(cb.equal(root.get("agentId"), agentId));
            }
            
            // 账号状态
            if (accountStatus != null) {
                predicates.add(cb.equal(root.get("accountStatus"), accountStatus));
            }
            
            // 资金冻结
            if (fundFreeze != null) {
                predicates.add(cb.equal(root.get("fundFreeze"), fundFreeze == 1));
            }
            
            // 余额负数
            if (negativeBalance != null && negativeBalance == 1) {
                predicates.add(cb.lessThan(root.get("currentBalance"), BigDecimal.ZERO));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        // 查询商户列表
        List<Merchant> merchants = merchantRepository.findAll(spec);
        
        // 导出为Excel
        return ExportUtil.exportMerchantsToExcel(merchants);
    }
}