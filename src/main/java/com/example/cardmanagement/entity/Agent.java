package com.example.cardmanagement.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name; // 代理名称

    @Column(name = "balance", columnDefinition = "decimal(20,2) default 0.00")
    private BigDecimal balance; // 代理余额

    @Column(name = "status", columnDefinition = "tinyint(1) default 1")
    private Integer status; // 代理状态

    @Column(name = "total_recharge_amount", columnDefinition = "decimal(20,2) default 0.00")
    private BigDecimal totalRechargeAmount; // 充值总金额

    @Column(name = "single_card_fee", columnDefinition = "decimal(10,2) default 0.00")
    private BigDecimal singleCardFee; // 单笔开卡费用

    @Column(name = "recharge_rate", columnDefinition = "decimal(10,4) default 0.0000")
    private BigDecimal rechargeRate; // 充值费率

    @Column(name = "create_time", columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private Date createTime; // 创建时间
}