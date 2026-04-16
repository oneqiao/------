package com.example.cardmanagement.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商户卡号DTO类
 * 用于接收商户卡号相关数据的传输对象
 */
@Data
public class MerchantCardDTO {

    private Long merchantId;
    private String cardNo;
    private Integer cardStatus;
    private BigDecimal cardBalance;
}