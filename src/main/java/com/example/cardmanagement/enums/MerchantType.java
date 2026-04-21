package com.example.cardmanagement.enums;

import java.util.Locale;

/**
 * 商户类型枚举。
 */
public enum MerchantType {
    WHITE("白量"),
    REFUND("退款");

    private final String description;

    MerchantType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据字符串值解析商户类型，大小写不敏感。
     *
     * @param value 枚举字符串
     * @return 商户类型
     */
    public static MerchantType fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("商户类型不能为空");
        }

        String normalizedValue = value.trim().toUpperCase(Locale.ROOT);
        for (MerchantType type : MerchantType.values()) {
            if (type.name().equals(normalizedValue)) {
                return type;
            }
        }

        throw new IllegalArgumentException("无效的商户类型，可选值：WHITE、REFUND");
    }
}
