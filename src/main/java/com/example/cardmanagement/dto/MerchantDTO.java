package com.example.cardmanagement.dto;

import lombok.Data;

/**
 * 商户请求 DTO。
 */
@Data
public class MerchantDTO {

    /**
     * 商户号。
     */
    private String merchantNo;

    /**
     * 商户名称。
     */
    private String name;

    /**
     * 商户类型，使用枚举字符串：WHITE / REFUND。
     */
    private String merchantType;

    /**
     * 代理 ID。
     */
    private Long agentId;

    /**
     * 登录账号。
     */
    private String loginAccount;

    /**
     * 登录密码。
     */
    private String loginPassword;

    /**
     * 账号状态：0 禁用，1 正常，2 删除。
     */
    private Integer accountStatus;

    /**
     * 资金是否冻结。
     */
    private Boolean fundFreeze;
}
