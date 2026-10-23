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
 * 「使った」記録を1回分ずつ保存するEntity。
 * どの在庫から使ったかは 品目名＋期限＋保存方法 の3つで表す（在庫のまとめ方と同じ）。
 * 買った記録の数量は書き換えず、この記録を積み上げて在庫を計算する。
 */
@Entity
@Table(name = "usages")
public class Usage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // どの在庫から使ったか（3つセットで在庫のまとまりを表す）
    @Column(nullable = false, length = 50)
    private String name;

    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StorageType storageType;

    // 使った数量（1個＝100の整数）
    @Column(nullable = false)
    private int quantity;

    // 使った日
    @Column(nullable = false)
    private LocalDate usedDate;

    // 何に使ったか（例：カレー）
    @Column(length = 50)
    private String purpose;

    @Column(length = 200)
    private String note;

    public Usage() {
    }

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

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public void setStorageType(StorageType storageType) {
        this.storageType = storageType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(LocalDate usedDate) {
        this.usedDate = usedDate;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
