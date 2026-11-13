package com.example.fridge_inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.example.fridge_inventory.entity.StorageType;

/**
 * 在庫1行分（品目名＋期限＋保存方法 のまとまり）。
 * remaining は「買った合計 − 使った合計」で、100倍の整数。
 */
public record StockItem(
        String name,
        String unit,
        LocalDate expiryDate,
        StorageType storageType,
        long remaining) {

    /**
     * 品目名＋期限＋保存方法 をつないだ「在庫の目印」を作る。
     * 例：にんじん|2026-11-20|REFRIGERATED（期限なしは真ん中が空）
     * 買った記録の番号を借りないので、記録を消しても目印は変わらない。
     */
    public static String keyOf(String name, LocalDate expiryDate, StorageType storageType) {
        String expiry = (expiryDate == null) ? "" : expiryDate.toString();
        return name + "|" + expiry + "|" + storageType.name();
    }

    /** この在庫の目印（プルダウンで選んだときに送られる値） */
    public String key() {
        return keyOf(name, expiryDate, storageType);
    }

    /** 画面表示用の残り数量。例：150 → "1.5" */
    public String quantityDisplay() {
        return BigDecimal.valueOf(remaining, 2).stripTrailingZeros().toPlainString();
    }

    /** 期限まであと何日か。期限なしなら null */
    public Long daysLeft() {
        if (expiryDate == null) {
            return null;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    /** 色分け用の状態。expired=期限切れ、soon=3日以内、ok=余裕あり、none=期限なし */
    public String status() {
        Long days = daysLeft();
        if (days == null) {
            return "none";
        }
        if (days < 0) {
            return "expired";
        }
        if (days <= 3) {
            return "soon";
        }
        return "ok";
    }

    /** プルダウンに出す文字。例：牛乳（期限 2026-10-30・冷蔵・残り 2 本） */
    public String label() {
        String expiry = (expiryDate == null) ? "期限なし" : "期限 " + expiryDate;
        return name + "（" + expiry + "・" + storageType.getLabel()
                + "・残り " + quantityDisplay() + " " + unit + "）";
    }
}
