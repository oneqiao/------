package com.example.cardmanagement.enums;

/**
 * 商户类型枚举类
 */
public enum MerchantType {
    WHITE(1, "白量"),
    REFUND(0, "退款");

    private final int code;
    private final String description;

    MerchantType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static MerchantType fromCode(int code) {
        for (MerchantType type : MerchantType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的商户类型代码");
    }
}