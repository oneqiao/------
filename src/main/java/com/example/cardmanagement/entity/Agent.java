package com.example.cardmanagement.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 代理实体类
 * 对应agent表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "agent")
public class Agent {
    
    /**
     * 代理ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 名称
     */
    @Column(name = "name", nullable = false)
    private String name;
    
    /**
     * 余额
     */
    @Column(name = "balance", columnDefinition = "decimal(20,2) default 0.00")
    private BigDecimal balance;
    
    /**
     * 状态 0禁用 1启用
     */
    @Column(name = "status", columnDefinition = "tinyint(1) default 1")
    private Integer status;
    
    /**
     * 充值总金额
     */
    @Column(name = "total_recharge_amount", columnDefinition = "decimal(20,2) default 0.00")
    private BigDecimal totalRechargeAmount;
    
    /**
     * 单笔开卡费用
     */
    @Column(name = "single_card_fee", columnDefinition = "decimal(10,2) default 0.00")
    private BigDecimal singleCardFee;
    
    /**
     * 充值费率
     */
    @Column(name = "recharge_rate", columnDefinition = "decimal(10,4) default 0.0000")
    private BigDecimal rechargeRate;
    
    /**
     * 创建时间
     */
    @Column(name = "create_time", columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private Date createTime;
}