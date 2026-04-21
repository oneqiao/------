package com.example.cardmanagement.service;

import com.example.cardmanagement.dto.MerchantDTO;
import com.example.cardmanagement.entity.Merchant;
import com.example.cardmanagement.enums.MerchantType;
import com.example.cardmanagement.repository.MerchantRepository;
import jakarta.persistence.criteria.Predicate;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商户服务类。
 */
@Service
public class MerchantService {

    private static final int DELETED_STATUS = 2;

    private final MerchantRepository merchantRepository;
    private final PasswordEncoder passwordEncoder;

    public MerchantService(MerchantRepository merchantRepository,
                           PasswordEncoder passwordEncoder) {
        this.merchantRepository = merchantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 分页查询商户列表。
     * 默认不查询已软删除的商户；如果显式传入 accountStatus，则按传入值过滤。
     */
    @Transactional(readOnly = true)
    public Page<Merchant> getMerchantList(Pageable pageable,
                                          String merchantNo,
                                          String name,
                                          String merchantType,
                                          Long agentId,
                                          Integer accountStatus,
                                          Integer fundFreeze,
                                          Integer negativeBalance) {
        return merchantRepository.findAll(
                buildMerchantSpecification(merchantNo, name, merchantType, agentId, accountStatus, fundFreeze, negativeBalance),
                pageable
        );
    }

    /**
     * 新增商户。
     */
    @Transactional
    public Merchant addMerchant(MerchantDTO merchantDTO) {
        if (merchantDTO.getMerchantNo() == null || merchantDTO.getMerchantNo().trim().isEmpty()) {
            throw new IllegalArgumentException("商户号不能为空");
        }
        if (merchantDTO.getName() == null || merchantDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("商户名称不能为空");
        }
        if (merchantDTO.getLoginAccount() == null || merchantDTO.getLoginAccount().trim().isEmpty()) {
            throw new IllegalArgumentException("登录账号不能为空");
        }
        if (merchantDTO.getLoginPassword() == null || merchantDTO.getLoginPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("登录密码不能为空");
        }

        String merchantNo = merchantDTO.getMerchantNo().trim();
        if (merchantRepository.findByMerchantNo(merchantNo) != null) {
            throw new IllegalArgumentException("商户号已存在");
        }

        Merchant merchant = new Merchant();
        merchant.setMerchantNo(merchantNo);
        merchant.setName(merchantDTO.getName().trim());
        merchant.setMerchantType(MerchantType.fromValue(merchantDTO.getMerchantType()));
        merchant.setAgentId(merchantDTO.getAgentId());
        merchant.setLoginAccount(merchantDTO.getLoginAccount().trim());
        merchant.setLoginPassword(passwordEncoder.encode(merchantDTO.getLoginPassword().trim()));
        merchant.setAccountStatus(merchantDTO.getAccountStatus() != null ? merchantDTO.getAccountStatus() : 1);
        merchant.setFundFreeze(merchantDTO.getFundFreeze() != null ? merchantDTO.getFundFreeze() : false);
        merchant.setCurrentBalance(BigDecimal.ZERO);
        merchant.setCardCount(0);
        merchant.setCardBalance(BigDecimal.ZERO);
        merchant.setCreateTime(new Date());

        return merchantRepository.save(merchant);
    }

    /**
     * 编辑商户。
     */
    @Transactional
    public Merchant updateMerchant(Long id, MerchantDTO merchantDTO) {
        Merchant existingMerchant = getActiveMerchant(id);

        if (merchantDTO.getName() != null && !merchantDTO.getName().trim().isEmpty()) {
            existingMerchant.setName(merchantDTO.getName().trim());
        }
        if (merchantDTO.getMerchantType() != null && !merchantDTO.getMerchantType().trim().isEmpty()) {
            existingMerchant.setMerchantType(MerchantType.fromValue(merchantDTO.getMerchantType()));
        }
        if (merchantDTO.getLoginAccount() != null && !merchantDTO.getLoginAccount().trim().isEmpty()) {
            existingMerchant.setLoginAccount(merchantDTO.getLoginAccount().trim());
        }
        if (merchantDTO.getLoginPassword() != null && !merchantDTO.getLoginPassword().trim().isEmpty()) {
            existingMerchant.setLoginPassword(passwordEncoder.encode(merchantDTO.getLoginPassword().trim()));
        }
        if (merchantDTO.getAccountStatus() != null) {
            existingMerchant.setAccountStatus(merchantDTO.getAccountStatus());
        }
        if (merchantDTO.getFundFreeze() != null) {
            existingMerchant.setFundFreeze(merchantDTO.getFundFreeze());
        }

        return merchantRepository.save(existingMerchant);
    }

    /**
     * 软删除商户。
     */
    @Transactional
    public void deleteMerchant(Long id) {
        Merchant existingMerchant = getActiveMerchant(id);
        existingMerchant.setAccountStatus(DELETED_STATUS);
        merchantRepository.save(existingMerchant);
        merchantRepository.flush();
    }

    /**
     * 查询商户详情。
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getMerchantDetail(Long id) {
        Merchant merchant = getActiveMerchant(id);
        Map<String, Object> result = new HashMap<>();
        result.put("merchant", merchant);
        return result;
    }

    /**
     * 调整商户余额。
     */
    @Transactional
    public Map<String, Object> adjustBalance(Long id, String type, BigDecimal amount, String reason) {
        Merchant merchant = getActiveMerchant(id);

        if (merchant.getFundFreeze()) {
            throw new IllegalArgumentException("商户资金已冻结，无法调整余额");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("调整金额必须大于 0");
        }

        BigDecimal currentBalance = merchant.getCurrentBalance();
        BigDecimal newBalance;

        if ("recharge".equals(type)) {
            newBalance = currentBalance.add(amount);
        } else if ("withdraw".equals(type)) {
            if (currentBalance.compareTo(amount) < 0) {
                throw new IllegalArgumentException("余额不足");
            }
            newBalance = currentBalance.subtract(amount);
        } else {
            throw new IllegalArgumentException("无效的操作类型");
        }

        merchant.setCurrentBalance(newBalance);
        merchantRepository.save(merchant);

        Map<String, Object> result = new HashMap<>();
        result.put("oldBalance", currentBalance);
        result.put("newBalance", newBalance);
        result.put("changeAmount", amount);
        result.put("reason", reason);
        return result;
    }

    /**
     * 导出商户列表。
     * 默认不导出已软删除的商户；如果显式传入 accountStatus，则按传入值过滤。
     */
    @Transactional(readOnly = true)
    public byte[] exportMerchants(String merchantNo,
                                  String name,
                                  String merchantType,
                                  Long agentId,
                                  Integer accountStatus,
                                  Integer fundFreeze,
                                  Integer negativeBalance) throws IOException {
        List<Merchant> merchants = merchantRepository.findAll(
                buildMerchantSpecification(merchantNo, name, merchantType, agentId, accountStatus, fundFreeze, negativeBalance)
        );

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("商户数据");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("商户号");
            headerRow.createCell(1).setCellValue("商户名称");
            headerRow.createCell(2).setCellValue("商户类型");
            headerRow.createCell(3).setCellValue("代理ID");
            headerRow.createCell(4).setCellValue("账号状态");

            int rowNum = 1;
            for (Merchant merchant : merchants) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(merchant.getMerchantNo());
                row.createCell(1).setCellValue(merchant.getName());
                row.createCell(2).setCellValue(merchant.getMerchantType().name());
                if (merchant.getAgentId() == null) {
                    row.createCell(3).setCellValue("");
                } else {
                    row.createCell(3).setCellValue(merchant.getAgentId());
                }
                row.createCell(4).setCellValue(merchant.getAccountStatus());
            }

            workbook.write(bos);
            return bos.toByteArray();
        }
    }

    private Merchant getActiveMerchant(Long id) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商户不存在"));

        if (merchant.getAccountStatus() != null && merchant.getAccountStatus() == DELETED_STATUS) {
            throw new IllegalArgumentException("商户不存在或已删除");
        }

        return merchant;
    }

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
                predicates.add(cb.equal(root.get("merchantType"), MerchantType.fromValue(merchantType)));
            }
            if (agentId != null) {
                predicates.add(cb.equal(root.get("agentId"), agentId));
            }
            if (accountStatus != null) {
                predicates.add(cb.equal(root.get("accountStatus"), accountStatus));
            } else {
                predicates.add(cb.notEqual(root.get("accountStatus"), DELETED_STATUS));
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
}
