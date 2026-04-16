package com.example.cardmanagement.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.util.Date;
import java.math.BigDecimal;

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
    
    /**
     * 商户ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 商户号
     */
    @Column(name = "merchant_no", nullable = false, unique = true)
    private String merchantNo;
    
    /**
     * 名称
     */
    @Column(name = "name", nullable = false)
    private String name;
    
    /**
     * 商户类型 white白量 refund退款
     */
    @Column(name = "merchant_type")
    private String merchantType;
    
    /**
     * 代理ID
     */
    @Column(name = "agent_id")
    private Long agentId;
    
    /**
     * 代理信息（关联关系）
     */
    @ManyToOne
    @JoinColumn(name = "agent_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Agent agent;
    
    /**
     * 登陆账号
     */
    @Column(name = "login_account", unique = true)
    private String loginAccount;
    
    /**
     * 登录密码
     */
    @Column(name = "login_password")
    private String loginPassword;
    
    /**
     * 帐号状态 0禁用 1正常 2删除
     */
    @Column(name = "account_status", columnDefinition = "tinyint default 1")
    private Integer accountStatus;
    
    /**
     * 资金冻结 0未冻结 1冻结
     */
    @Column(name = "fund_freeze", columnDefinition = "tinyint(1) default 0")
    private Boolean fundFreeze;
    
    /**
     * 当前余额
     */
    @Column(name = "current_balance", columnDefinition = "decimal(20,2) default 0.00")
    private BigDecimal currentBalance;
    
    /**
     * 开卡数
     */
    @Column(name = "card_count", columnDefinition = "int default 0")
    private Integer cardCount;
    
    /**
     * 卡内余额
     */
    @Column(name = "card_balance", columnDefinition = "decimal(20,2) default 0.00")
    private BigDecimal cardBalance;
    
    /**
     * 卡号列表(JSON格式)
     */
    @Column(name = "card_list", columnDefinition = "text")
    private String cardList;
    
    /**
     * 创建时间
     */
    @Column(name = "create_time", columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private Date createTime;
}