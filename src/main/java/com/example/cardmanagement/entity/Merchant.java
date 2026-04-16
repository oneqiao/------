package com.example.cardmanagement.entity;

import com.example.cardmanagement.enums.MerchantType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商户实体类
 */
@Data
@Entity
@Table(name = "merchant")
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "merchant_no", nullable = false, unique = true)
    private String merchantNo;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "merchant_type", nullable = false)
    private MerchantType merchantType;  // 使用 MerchantType 枚举类型

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private Agent agent;  // 代理对象

    @Column(name = "agent_id", insertable = false, updatable = false)
    private Long agentId;  // 存储在数据库中的代理ID

    @Column(name = "login_account", nullable = false)
    private String loginAccount;

    @Column(name = "login_password", nullable = false)
    private String loginPassword; // 登录密码

    @Column(name = "account_status", nullable = false)
    private Integer accountStatus;  // 账号状态（0 禁用，1 启用 2 删除）

    @Column(name = "fund_freeze", nullable = false)
    private Boolean fundFreeze;  // 是否冻结

    @Column(name = "current_balance", nullable = false)
    private BigDecimal currentBalance;  // 当前余额

    @Column(name = "card_count", nullable = false)
    private Integer cardCount;  // 卡片数量

    @Column(name = "card_balance", nullable = false)
    private BigDecimal cardBalance;  // 卡内余额

    @Column(name = "create_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;  // 创建时间
}