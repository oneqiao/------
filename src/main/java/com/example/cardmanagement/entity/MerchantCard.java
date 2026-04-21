package com.example.cardmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商户卡实体类，对应 merchant_card 表。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "merchant_card")
public class MerchantCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 商户 ID。
     */
    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;

    /**
     * 关联商户对象。
     */
    @ManyToOne
    @JoinColumn(name = "merchant_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Merchant merchant;

    /**
     * 卡号。
     */
    @Column(name = "card_no", nullable = false, unique = true)
    private String cardNo;

    /**
     * 卡状态：0 禁用，1 正常。
     */
    @Column(name = "card_status", columnDefinition = "tinyint default 1")
    private Integer cardStatus;

    /**
     * 单卡余额。
     */
    @Column(name = "card_balance", columnDefinition = "decimal(20,2) default 0.00")
    private BigDecimal cardBalance;

    /**
     * 创建时间。
     */
    @Column(name = "create_time", columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private Date createTime;
}
