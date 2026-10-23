package com.example.fridge_inventory.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 「買った」記録を1回分ずつ保存するEntity。
 * 同じ品目を何度買っても、その都度1行ずつ増える（数量を書き換えない）。
 * 在庫数は「買った合計 − 使った合計」で計算する。
 */
@Entity
@Table(name = "purchases")
public class Purchase {

    // 主キー。DBが自動で1,2,3...と採番する
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 品目名（例：牛乳）
    @Column(nullable = false, length = 50)
    private String name;

    // 数量は「1個＝100」の整数で保存する（0.5個＝50、2.25個＝225）
    // 小数を使わないので計算誤差が出ない。画面に出すときは100で割る
    @Column(nullable = false)
    private int quantity;

    // 単位（例：本、g、個）
    @Column(nullable = false, length = 10)
    private String unit;

    // カテゴリ（まずは文字列。後で別テーブルに切り出す予定）
    @Column(nullable = false, length = 20)
    private String category;

    // 保存方法。STRINGで保存しておくと、enumの並び順を変えてもデータが壊れない
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StorageType storageType;

    // 消費期限。調味料などは期限なしもあるので空欄OK
    private LocalDate expiryDate;

    // 購入日
    @Column(nullable = false)
    private LocalDate purchasedDate;

    // 備考
    @Column(length = 200)
    private String note;

    // JPAが使う引数なしコンストラクタ（必須）
    public Purchase() {
    }

    /**
     * 画面表示用の数量（100で割った値）。例：50 → "0.5"、200 → "2"
     */
    public String getQuantityDisplay() {
        return BigDecimal.valueOf(quantity, 2).stripTrailingZeros().toPlainString();
    }

    // ---- getter / setter ----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public void setStorageType(StorageType storageType) {
        this.storageType = storageType;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDate getPurchasedDate() {
        return purchasedDate;
    }

    public void setPurchasedDate(LocalDate purchasedDate) {
        this.purchasedDate = purchasedDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}