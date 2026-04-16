package com.example.cardmanagement.service;

import com.example.cardmanagement.entity.Merchant;
import com.example.cardmanagement.entity.MerchantCard;
import com.example.cardmanagement.repository.MerchantCardRepository;
import com.example.cardmanagement.repository.MerchantRepository;
import com.example.cardmanagement.util.ExportUtil;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商户服务类
 * 处理商户相关的业务逻辑
 */
@Service
@Transactional(readOnly = true)
public class MerchantService {

    private final MerchantRepository merchantRepository;
    private final MerchantCardRepository merchantCardRepository;

    public MerchantService(MerchantRepository merchantRepository,
                           MerchantCardRepository merchantCardRepository) {
        this.merchantRepository = merchantRepository;
        this.merchantCardRepository = merchantCardRepository;
    }

    /**
     * 分页查询商户列表
     *
     * @param pageable        分页参数
     * @param merchantNo      商户号
     * @param name            商户名称
     * @param merchantType    商户类型
     * @param agentId         代理ID
     * @param accountStatus   账号状态
     * @param fundFreeze      资金冻结
     * @param negativeBalance 余额负数
     * @return 分页商户列表
     */
    @NonNull
    public Page<Map<String, Object>> getMerchantList(@NonNull Pageable pageable,
                                                     String merchantNo,
                                                     String name,
                                                     String merchantType,
                                                     Long agentId,
                                                     Integer accountStatus,
                                                     Integer fundFreeze,
                                                     Integer negativeBalance) {
        Specification<Merchant> spec = buildMerchantSpecification(
                merchantNo, name, merchantType, agentId, accountStatus, fundFreeze, negativeBalance
        );

        Page<Merchant> merchants = merchantRepository.findAll(spec, pageable);

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
            map.put("accountStatusText", getAccountStatusText(merchant.getAccountStatus()));
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
     *
     * @param id 商户ID
     * @return 商户详情，包含卡片列表
     */
    @NonNull
    public Map<String, Object> getMerchantDetail(@NonNull Long id) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商户不存在"));

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

        List<MerchantCard> cards = merchantCardRepository.findByMerchantId(id);
        map.put("cardList", cards);

        return map;
    }

    /**
     * 新增商户
     *
     * @param merchant 商户对象
     * @return 保存后的商户对象
     */
    @Transactional
    @NonNull
    public Merchant addMerchant(@NonNull Merchant merchant) {
        if (merchant.getMerchantNo() == null || merchant.getMerchantNo().trim().isEmpty()) {
            throw new IllegalArgumentException("商户号不能为空");
        }
        if (merchant.getName() == null || merchant.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("商户名称不能为空");
        }

        String merchantNo = merchant.getMerchantNo().trim();
        merchant.setMerchantNo(merchantNo);

        if (merchantRepository.findByMerchantNo(merchantNo) != null) {
            throw new IllegalArgumentException("商户号已存在");
        }

        if (merchant.getLoginAccount() != null && !merchant.getLoginAccount().trim().isEmpty()) {
            String loginAccount = merchant.getLoginAccount().trim();
            merchant.setLoginAccount(loginAccount);

            if (merchantRepository.findByLoginAccount(loginAccount) != null) {
                throw new IllegalArgumentException("登录账号已存在");
            }
        }

        if (merchant.getAccountStatus() == null) {
            merchant.setAccountStatus(1);
        }
        if (merchant.getFundFreeze() == null) {
            merchant.setFundFreeze(false);
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
     *
     * @param id       商户ID
     * @param merchant 商户对象
     * @return 更新后的商户对象
     */
    @Transactional
    @NonNull
    public Merchant updateMerchant(@NonNull Long id, @NonNull Merchant merchant) {
        Merchant existingMerchant = merchantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商户不存在"));

        if (merchant.getName() != null && !merchant.getName().trim().isEmpty()) {
            existingMerchant.setName(merchant.getName().trim());
        }
        if (merchant.getMerchantType() != null) {
            existingMerchant.setMerchantType(merchant.getMerchantType().trim());
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
     *
     * @param id 商户ID
     */
    @Transactional
    public void deleteMerchant(@NonNull Long id) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商户不存在"));

        merchant.setAccountStatus(2);
        merchantRepository.save(merchant);
    }

    /**
     * 调整商户余额
     *
     * @param id     商户ID
     * @param type   操作类型：recharge / withdraw
     * @param amount 调整金额
     * @param reason 调整原因
     * @return 调整结果
     */
    @Transactional
    @NonNull
    public Map<String, Object> adjustBalance(@NonNull Long id,
                                             @NonNull String type,
                                             @NonNull BigDecimal amount,
                                             String reason) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商户不存在"));

        if (Boolean.TRUE.equals(merchant.getFundFreeze())) {
            throw new IllegalArgumentException("商户资金已冻结，无法调整余额");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("调整金额必须大于0");
        }

        String trimType = type.trim();
        if (!"recharge".equals(trimType) && !"withdraw".equals(trimType)) {
            throw new IllegalArgumentException("操作类型无效");
        }

        BigDecimal oldBalance = merchant.getCurrentBalance() != null
                ? merchant.getCurrentBalance()
                : BigDecimal.ZERO;

        BigDecimal newBalance;
        if ("recharge".equals(trimType)) {
            newBalance = oldBalance.add(amount);
        } else {
            if (oldBalance.compareTo(amount) < 0) {
                throw new IllegalArgumentException("余额不足");
            }
            newBalance = oldBalance.subtract(amount);
        }

        merchant.setCurrentBalance(newBalance);
        merchantRepository.save(merchant);

        Map<String, Object> result = new HashMap<>();
        result.put("oldBalance", oldBalance);
        result.put("newBalance", newBalance);
        result.put("changeAmount", amount);
        result.put("reason", reason);

        return result;
    }

    /**
     * 导出商户列表为Excel
     *
     * @param merchantNo      商户号
     * @param name            商户名称
     * @param merchantType    商户类型
     * @param agentId         代理ID
     * @param accountStatus   账号状态
     * @param fundFreeze      资金冻结
     * @param negativeBalance 余额负数
     * @return Excel 文件字节数组
     * @throws IOException IO异常
     */
    @NonNull
    public byte[] exportMerchants(String merchantNo,
                                  String name,
                                  String merchantType,
                                  Long agentId,
                                  Integer accountStatus,
                                  Integer fundFreeze,
                                  Integer negativeBalance) throws IOException {
        Specification<Merchant> spec = buildMerchantSpecification(
                merchantNo, name, merchantType, agentId, accountStatus, fundFreeze, negativeBalance
        );

        List<Merchant> merchants = merchantRepository.findAll(spec);
        return ExportUtil.exportMerchantsToExcel(merchants);
    }

    /**
     * 构建商户查询条件
     */
    private Specification<Merchant> buildMerchantSpecification(String merchantNo,
                                                               String name,
                                                               String merchantType,
                                                               Long agentId,
                                                               Integer accountStatus,
                                                               Integer fundFreeze,
                                                               Integer negativeBalance) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (merchantNo != null && !merchantNo.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("merchantNo"), merchantNo.trim()));
            }

            if (name != null && !name.trim().isEmpty()) {
                predicates.add(cb.like(root.get("name"), "%" + name.trim() + "%"));
            }

            if (merchantType != null && !merchantType.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("merchantType"), merchantType.trim()));
            }

            if (agentId != null) {
                predicates.add(cb.equal(root.get("agentId"), agentId));
            }

            if (accountStatus != null) {
                predicates.add(cb.equal(root.get("accountStatus"), accountStatus));
            }

            if (fundFreeze != null) {
                predicates.add(cb.equal(root.get("fundFreeze"), fundFreeze == 1));
            }

            if (negativeBalance != null && negativeBalance == 1) {
                predicates.add(cb.lessThan(root.get("currentBalance"), BigDecimal.ZERO));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 账号状态转文字
     */
    private String getAccountStatusText(Integer accountStatus) {
        if (accountStatus == null) {
            return "";
        }

        switch (accountStatus) {
            case 0:
                return "禁用";
            case 1:
                return "正常";
            case 2:
                return "删除";
            default:
                return "";
        }
    }
}