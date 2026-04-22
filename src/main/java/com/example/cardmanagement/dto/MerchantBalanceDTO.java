package com.example.cardmanagement.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商户余额调整请求 DTO。
 */
@Data
public class MerchantBalanceDTO {

    /**
     * 调整类型：recharge / withdraw。
     */
    private String type;

    /**
     * 调整金额。
     */
    private BigDecimal amount;

    /**
     * 调整原因。
     */
    private String reason;
}
