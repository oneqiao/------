package com.example.cardmanagement.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商户实体类
 * 对应merchant表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "merchant")
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "merchant_no", nullable = false, unique = true)
    private String merchantNo; // 商户号

    @Column(name = "name", nullable = false)
    private String name; // 商户名称

    @Column(name = "merchant_type")
    private String merchantType; // 商户类型（例如 white、refund）

    @Column(name = "agent_id")
    private Long agentId; // 代理ID

    @ManyToOne
    @JoinColumn(name = "agent_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Agent agent; // 代理信息（关联关系）

    @Column(name = "login_account", unique = true)
    private String loginAccount; // 登录账号

    @Column(name = "login_password")
    private String loginPassword; // 登录密码

    @Column(name = "account_status", columnDefinition = "tinyint default 1")
    private Integer accountStatus; // 账号状态

    @Column(name = "fund_freeze", columnDefinition = "tinyint(1) default 0")
    private Boolean fundFreeze; // 资金冻结状态

    @Column(name = "current_balance", columnDefinition = "decimal(20,2) default 0.00")
    private BigDecimal currentBalance; // 当前余额

    @Column(name = "card_count", columnDefinition = "int default 0")
    private Integer cardCount; // 开卡数

    @Column(name = "card_balance", columnDefinition = "decimal(20,2) default 0.00")
    private BigDecimal cardBalance; // 卡内余额

    @Column(name = "card_list", columnDefinition = "text")
    private String cardList; // 卡号列表（JSON格式）

    @Column(name = "create_time", columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private Date createTime; // 创建时间
}