package com.example.fridge_inventory.dto;

import java.time.LocalDate;

import com.example.fridge_inventory.entity.StorageType;

/**
 * 「買った」記録を 品目名＋期限＋保存方法 ごとに合計した結果。
 * total は100倍の整数の合計。
 */
public record PurchaseTotal(
        String name,
        String unit,
        LocalDate expiryDate,
        StorageType storageType,
        Long total) {
}
