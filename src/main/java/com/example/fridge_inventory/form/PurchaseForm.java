package com.example.fridge_inventory.form;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.fridge_inventory.entity.Purchase;
import com.example.fridge_inventory.entity.StorageType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 「買った」画面の入力を受け取るためのクラス。
 * 画面では 0.5 のような普通の数で入力し、保存するときに100倍の整数へ変換する。
 * 入力チェックはここで行い、Entityは保存の形だけに集中させる。
 */
public class PurchaseForm {

    @NotBlank(message = "品目名を入力してください")
    @Size(max = 50, message = "品目名は50文字以内で入力してください")
    @Pattern(regexp = "[^|]*", message = "品目名に「|」は使えません")
    private String name;

    @NotNull(message = "数量を入力してください")
    @DecimalMin(value = "0.01", message = "数量は0より大きい値を入力してください")
    @Digits(integer = 6, fraction = 2, message = "数量は小数第2位までで入力してください")
    private BigDecimal quantity;

    @NotBlank(message = "単位を入力してください")
    @Size(max = 10, message = "単位は10文字以内で入力してください")
    private String unit;

    @NotBlank(message = "カテゴリを入力してください")
    @Size(max = 20, message = "カテゴリは20文字以内で入力してください")
    private String category;

    @NotNull(message = "保存方法を選んでください")
    private StorageType storageType;

    // 空欄OK
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate expiryDate;

    // 初期値は今日
    @NotNull(message = "購入日を入力してください")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate purchasedDate = LocalDate.now();

    @Size(max = 200, message = "備考は200文字以内で入力してください")
    private String note;

    /**
     * 入力内容をEntityに詰め替える。数量はここで100倍の整数にする。
     * 例：0.5 → 50、2 → 200
     */
    public Purchase toEntity() {
        Purchase purchase = new Purchase();
        purchase.setName(name);
        purchase.setQuantity(quantity.movePointRight(2).intValueExact());
        purchase.setUnit(unit);
        purchase.setCategory(category);
        purchase.setStorageType(storageType);
        purchase.setExpiryDate(expiryDate);
        purchase.setPurchasedDate(purchasedDate);
        purchase.setNote(note);
        return purchase;
    }

    // ---- getter / setter ----

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
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
