package com.example.cardmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 代理实体类
 * 对应代理表
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
    private String name;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "total_recharge_amount", nullable = false)
    private BigDecimal totalRechargeAmount;

    @Column(name = "single_card_fee", nullable = false)
    private BigDecimal singleCardFee;

    @Column(name = "recharge_rate", nullable = false)
    private BigDecimal rechargeRate;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    /**
     * 在插入数据之前自动设置当前时间
     */
    @PrePersist
    public void prePersist() {
        if (this.createTime == null) {
            this.createTime = new Date();  // 设置当前时间
        }
    }
}