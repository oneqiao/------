package com.example.cardmanagement.entity;

import com.example.cardmanagement.enums.MerchantType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商户实体类。
 */
@Data
@Entity
@Table(name = "merchant")
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 商户号。
     */
    @Column(name = "merchant_no", nullable = false, unique = true)
    private String merchantNo;

    /**
     * 商户名称。
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * 商户类型，按枚举字符串存储。
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "merchant_type", nullable = false)
    private MerchantType merchantType;

    /**
     * 关联代理对象。
     */
    @ManyToOne
    @JoinColumn(name = "agent_id")
    private Agent agent;

    /**
     * 代理 ID。
     */
    @Column(name = "agent_id", insertable = false, updatable = false)
    private Long agentId;

    /**
     * 登录账号。
     */
    @Column(name = "login_account", nullable = false)
    private String loginAccount;

    /**
     * 登录密码。
     */
    @Column(name = "login_password", nullable = false)
    private String loginPassword;

    /**
     * 账号状态：0 禁用，1 正常，2 删除。
     */
    @Column(name = "account_status", nullable = false)
    private Integer accountStatus;

    /**
     * 是否冻结资金。
     */
    @Column(name = "fund_freeze", nullable = false)
    private Boolean fundFreeze;

    /**
     * 当前余额。
     */
    @Column(name = "current_balance", nullable = false)
    private BigDecimal currentBalance;

    /**
     * 卡数量。
     */
    @Column(name = "card_count", nullable = false)
    private Integer cardCount;

    /**
     * 卡内余额汇总。
     */
    @Column(name = "card_balance", nullable = false)
    private BigDecimal cardBalance;

    /**
     * 创建时间。
     */
    @Column(name = "create_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
}
