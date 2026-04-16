package com.example.cardmanagement.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商户视图对象（VO）
 */
@Data
public class MerchantVO {

    private Long id;
    private String merchantNo;
    private String name;
    private String merchantTypeDescription; // 显示商户类型描述
    private Long agentId;
    private String agentName;
    private String loginAccount;
    private Integer accountStatus;
    private String accountStatusDescription; // 显示账号状态描述
    private Boolean fundFreeze;
    private BigDecimal currentBalance;
    private Integer cardCount;
    private BigDecimal cardBalance;
    private Date createTime;

}