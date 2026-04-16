package com.example.cardmanagement.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商户VO类
 * 用于商户数据的显示和响应
 */
@Data
public class MerchantVO {

    private Long id;
    private String merchantNo;
    private String name;
    private String merchantType;
    private Long agentId;
    private String agentName;
    private String loginAccount;
    private Integer accountStatus;
    private String accountStatusText;
    private Boolean fundFreeze;
    private BigDecimal currentBalance;
    private Integer cardCount;
    private BigDecimal cardBalance;
    private Date createTime;
}