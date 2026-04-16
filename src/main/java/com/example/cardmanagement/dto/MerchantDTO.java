package com.example.cardmanagement.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商户DTO类
 * 用于接收商户相关数据的传输对象
 */
@Data
public class MerchantDTO {

    private String merchantNo;
    private String name;
    private String merchantType;
    private Long agentId;
    private String loginAccount;
    private String loginPassword;
    private Integer accountStatus;
    private Boolean fundFreeze;
    private BigDecimal currentBalance;
    private Integer cardCount;
    private BigDecimal cardBalance;
}