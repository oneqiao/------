package com.example.cardmanagement.service;

import com.example.cardmanagement.dto.MerchantDTO;
import com.example.cardmanagement.entity.Merchant;
import com.example.cardmanagement.enums.MerchantType;
import com.example.cardmanagement.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;


/**
 * 商户服务类
 */
@Service
public class MerchantService {

    @Autowired
    private MerchantRepository merchantRepository;

    /**
     * 分页查询商户列表
     * @param pageable 分页请求
     * @param merchantNo 商户号
     * @param name 商户名称
     * @param merchantType 商户类型
     * @param agentId 代理ID
     * @param accountStatus 账号状态
     * @param fundFreeze 资金冻结
     * @param negativeBalance 是否余额负数
     * @return 分页商户列表
     */
    @Transactional
    public Page<Merchant> getMerchantList(Pageable pageable, String merchantNo, String name, Integer merchantType,
                                          Long agentId, Integer accountStatus, Integer fundFreeze, Integer negativeBalance) {
        // 实现分页查询的过滤逻辑
        return merchantRepository.findAll((root, query, cb) -> {
            var predicates = new ArrayList<>();

            if (merchantNo != null && !merchantNo.isEmpty()) {
                predicates.add(cb.equal(root.get("merchantNo"), merchantNo));
            }
            if (name != null && !name.isEmpty()) {
                predicates.add(cb.like(root.get("name"), "%" + name + "%"));
            }
            if (merchantType != null) {
                predicates.add(cb.equal(root.get("merchantType"), merchantType));
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
        }, pageable);
    }


    /**
     * 新增商户
     *
     * @param merchantDTO 商户数据传输对象
     * @return 新增的商户对象
     */
    @Transactional
    public Merchant addMerchant(MerchantDTO merchantDTO) {
        if (merchantDTO.getMerchantNo() == null || merchantDTO.getMerchantNo().trim().isEmpty()) {
            throw new IllegalArgumentException("商户号不能为空");
        }

        if (merchantDTO.getName() == null || merchantDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("商户名称不能为空");
        }

        // 校验商户号是否已存在
        if (merchantRepository.findByMerchantNo(merchantDTO.getMerchantNo()) != null) {
            throw new IllegalArgumentException("商户号已存在");
        }

        // 创建商户实体并设置字段
        Merchant merchant = new Merchant();
        merchant.setMerchantNo(merchantDTO.getMerchantNo().trim());
        merchant.setName(merchantDTO.getName().trim());
        merchant.setMerchantType(MerchantType.fromCode(merchantDTO.getMerchantType())); // 使用枚举类型
        merchant.setAgentId(merchantDTO.getAgentId());
        merchant.setLoginAccount(merchantDTO.getLoginAccount().trim());
        merchant.setLoginPassword(merchantDTO.getLoginPassword());  // 密码处理
        merchant.setAccountStatus(merchantDTO.getAccountStatus() != null ? merchantDTO.getAccountStatus() : 1);  // 默认启用
        merchant.setFundFreeze(merchantDTO.getFundFreeze() != null ? merchantDTO.getFundFreeze() : false);  // 默认不冻结
        merchant.setCurrentBalance(BigDecimal.ZERO);  // 默认余额为 0
        merchant.setCardCount(0);  // 默认卡片数量为 0
        merchant.setCardBalance(BigDecimal.ZERO);  // 默认卡内余额为 0
        merchant.setCreateTime(new Date());  // 当前时间

        return merchantRepository.save(merchant);  // 保存并返回
    }

    /**
     * 编辑商户
     */
    @Transactional
    public Merchant updateMerchant(Long id, Merchant merchant) {
        Merchant existingMerchant = merchantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商户不存在"));

        if (merchant.getName() != null && !merchant.getName().trim().isEmpty()) {
            existingMerchant.setName(merchant.getName().trim());
        }
        if (merchant.getMerchantType() != null) {
            existingMerchant.setMerchantType(merchant.getMerchantType());
        }
        if (merchant.getLoginAccount() != null && !merchant.getLoginAccount().trim().isEmpty()) {
            existingMerchant.setLoginAccount(merchant.getLoginAccount().trim());
        }
        if (merchant.getAccountStatus() != null) {
            existingMerchant.setAccountStatus(merchant.getAccountStatus());
        }

        return merchantRepository.save(existingMerchant);
    }

    /**
     * 删除商户（软删除）
     */
    @Transactional
    public void deleteMerchant(Long id) {
        Merchant existingMerchant = merchantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商户不存在"));

        existingMerchant.setAccountStatus(2);  // 标记为删除状态
        merchantRepository.save(existingMerchant);

        // 强制刷新数据到数据库
        merchantRepository.flush();  // 刷新数据库
    }

    /**
     * 查询商户详情
     */
    public Map<String, Object> getMerchantDetail(Long id) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商户不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("merchant", merchant);
        return result;
    }

    @Transactional
    public Map<String, Object> adjustBalance(Long id, String type, BigDecimal amount, String reason) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商户不存在"));

        if (merchant.getFundFreeze()) {
            throw new IllegalArgumentException("商户资金已冻结，无法调整余额");
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

        return result;
    }

    @Transactional
    public byte[] exportMerchants(String merchantNo, String name, Integer merchantType, Long agentId,
                                  Integer accountStatus, Integer fundFreeze, Integer negativeBalance) throws IOException {
        List<Merchant> merchants = merchantRepository.findAll((root, query, cb) -> {
            var predicates = new ArrayList<>();

            if (merchantNo != null && !merchantNo.isEmpty()) {
                predicates.add(cb.equal(root.get("merchantNo"), merchantNo));
            }
            if (name != null && !name.isEmpty()) {
                predicates.add(cb.like(root.get("name"), "%" + name + "%"));
            }
            if (merchantType != null) {
                predicates.add(cb.equal(root.get("merchantType"), merchantType));
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
        });

        Workbook workbook = new XSSFWorkbook();
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
            row.createCell(2).setCellValue(merchant.getMerchantType().getDescription());
            row.createCell(3).setCellValue(merchant.getAgentId());
            row.createCell(4).setCellValue(merchant.getAccountStatus());
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();

        return bos.toByteArray();
    }

}