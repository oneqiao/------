package com.example.cardmanagement.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 代理响应 VO。
 */
@Data
public class AgentVO {

    private Long id;
    private String name;
    private BigDecimal balance;
    private Integer status;
    private BigDecimal totalRechargeAmount;
    private BigDecimal singleCardFee;
    private BigDecimal rechargeRate;
    private Date createTime;
    private Long merchantCount;
}
