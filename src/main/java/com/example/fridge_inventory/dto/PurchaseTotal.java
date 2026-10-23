package com.example.fridge_inventory.dto;

import java.time.LocalDate;

import com.example.fridge_inventory.entity.StorageType;

/**
 * 「買った」記録を 品目名＋期限＋保存方法 ごとに合計した結果。
 * representativeId はそのまとまりの中で一番小さいID（使った画面で選ぶときの目印）。
 * total は100倍の整数の合計。
 */
public record PurchaseTotal(
        Long representativeId,
        String name,
        String unit,
        LocalDate expiryDate,
        StorageType storageType,
        Long total) {
}
