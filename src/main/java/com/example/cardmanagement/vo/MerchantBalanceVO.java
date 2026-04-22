package com.example.cardmanagement.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商户余额调整响应 VO。
 */
@Data
public class MerchantBalanceVO {

    private BigDecimal oldBalance;
    private BigDecimal newBalance;
    private BigDecimal changeAmount;
    private String reason;
}
