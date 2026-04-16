package com.example.cardmanagement.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 商户卡号实体类
 * 对应merchant_card表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "merchant_card")
public class MerchantCard {
    
    /**
     * 卡片ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 商户ID
     */
    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;
    
    /**
     * 商户信息（关联关系）
     */
    @ManyToOne
    @JoinColumn(name = "merchant_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Merchant merchant;
    
    /**
     * 卡号
     */
    @Column(name = "card_no", nullable = false, unique = true)
    private String cardNo;
    
    /**
     * 卡状态 0禁用 1正常
     */
    @Column(name = "card_status", columnDefinition = "tinyint default 1")
    private Integer cardStatus;
    
    /**
     * 单卡余额
     */
    @Column(name = "card_balance", columnDefinition = "decimal(20,2) default 0.00")
    private BigDecimal cardBalance;
    
    /**
     * 创建时间
     */
    @Column(name = "create_time", columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private Date createTime;
}