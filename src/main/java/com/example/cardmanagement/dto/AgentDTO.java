package com.example.cardmanagement.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 代理请求 DTO。
 */
@Data
public class AgentDTO {

    /**
     * 代理名称。
     */
    private String name;

    /**
     * 余额。
     */
    private BigDecimal balance;

    /**
     * 状态：0 禁用，1 启用。
     */
    private Integer status;

    /**
     * 累计充值金额。
     */
    private BigDecimal totalRechargeAmount;

    /**
     * 单卡费用。
     */
    private BigDecimal singleCardFee;

    /**
     * 充值费率。
     */
    private BigDecimal rechargeRate;
}
