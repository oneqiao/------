package com.example.cardmanagement.dto;

import lombok.Data;

/**
 * 商户数据传输对象（DTO）
 */
@Data
public class MerchantDTO {

    private String merchantNo;
    private String name;
    private Integer merchantType; // 商户类型（0=refund, 1=white）
    private Long agentId;
    private String loginAccount;
    private String loginPassword; // 密码字段
    private Integer accountStatus; // 账号状态（0=禁用，1=启用）
    private Boolean fundFreeze; // 资金冻结

}