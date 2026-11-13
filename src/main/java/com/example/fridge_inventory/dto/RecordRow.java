package com.example.fridge_inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.fridge_inventory.entity.StorageType;

/**
 * 「記録の編集」画面の表1行分。
 * 買った記録と使った記録を同じ形にそろえて、1つの表に混ぜて並べるためのもの。
 * quantity は100倍の整数で、使った記録はマイナスで持つ（例：1本使った → -100）。
 */
public record RecordRow(
        Type type,
        Long id,
        LocalDate date,
        String name,
        long quantity,
        String unit,
        StorageType storageType,
        LocalDate expiryDate,
        String memo) {

    public enum Type { PURCHASE, USAGE }

    public boolean isPurchase() {
        return type == Type.PURCHASE;
    }

    public String typeLabel() {
        return isPurchase() ? "買った" : "使った";
    }

    /** 画面表示用の数量。例：+3、−0.5 */
    public String quantityDisplay() {
        String number = BigDecimal.valueOf(Math.abs(quantity), 2).stripTrailingZeros().toPlainString();
        return (quantity >= 0 ? "+" : "−") + number;
    }

    /** 行を押したときに開く詳細画面のURL */
    public String detailPath() {
        return isPurchase() ? "/purchases/" + id : "/usages/" + id;
    }
}
