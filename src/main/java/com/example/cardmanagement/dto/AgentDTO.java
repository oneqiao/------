package com.example.cardmanagement.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 代理DTO类
 * 用于接收代理相关数据的传输对象
 */
@Data
public class AgentDTO {

    private String name;
    private BigDecimal balance;
    private Integer status;
    private BigDecimal totalRechargeAmount;
    private BigDecimal singleCardFee;
    private BigDecimal rechargeRate;
}