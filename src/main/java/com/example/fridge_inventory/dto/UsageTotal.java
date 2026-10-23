package com.example.fridge_inventory.dto;

import java.time.LocalDate;

import com.example.fridge_inventory.entity.StorageType;

/**
 * 「使った」記録を 品目名＋期限＋保存方法 ごとに合計した結果。
 * total は100倍の整数の合計。
 */
public record UsageTotal(
        String name,
        LocalDate expiryDate,
        StorageType storageType,
        Long total) {
}
