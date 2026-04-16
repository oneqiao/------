package com.example.cardmanagement.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "merchant_id", nullable = false)
    private Long merchantId; // 商户ID

    @ManyToOne
    @JoinColumn(name = "merchant_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Merchant merchant; // 商户信息（关联关系）

    @Column(name = "card_no", nullable = false, unique = true)
    private String cardNo; // 卡号

    @Column(name = "card_status", columnDefinition = "tinyint default 1")
    private Integer cardStatus; // 卡状态

    @Column(name = "card_balance", columnDefinition = "decimal(20,2) default 0.00")
    private BigDecimal cardBalance; // 单卡余额

    @Column(name = "create_time", columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private Date createTime; // 创建时间
}