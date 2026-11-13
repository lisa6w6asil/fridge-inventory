package com.example.fridge_inventory.form;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.fridge_inventory.dto.StockItem;
import com.example.fridge_inventory.entity.Usage;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 「使った」画面の入力を受け取るクラス。
 * 食材は手入力ではなく、在庫一覧（プルダウン）から選ぶ。
 */
public class UsageForm {

    // プルダウンで選んだ在庫の目印（StockItem.key()）
    @NotBlank(message = "使った食材を選んでください")
    private String stockKey;

    @NotNull(message = "数量を入力してください")
    @DecimalMin(value = "0.01", message = "数量は0より大きい値を入力してください")
    @Digits(integer = 6, fraction = 2, message = "数量は小数第2位までで入力してください")
    private BigDecimal quantity;

    @NotNull(message = "使った日を入力してください")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate usedDate = LocalDate.now();

    @Size(max = 50, message = "「何に」は50文字以内で入力してください")
    private String purpose;

    @Size(max = 200, message = "備考は200文字以内で入力してください")
    private String note;

    /** 入力された数量を100倍の整数にする。例：0.5 → 50 */
    public int quantityInHundredths() {
        return quantity.movePointRight(2).intValueExact();
    }

    /** 選んだ在庫の情報と入力内容から、保存用のEntityを作る */
    public Usage toEntity(StockItem stock) {
        Usage usage = new Usage();
        usage.setName(stock.name());
        usage.setExpiryDate(stock.expiryDate());
        usage.setStorageType(stock.storageType());
        usage.setQuantity(quantityInHundredths());
        usage.setUsedDate(usedDate);
        usage.setPurpose(purpose);
        usage.setNote(note);
        return usage;
    }

    // ---- getter / setter ----

    public String getStockKey() {
        return stockKey;
    }

    public void setStockKey(String stockKey) {
        this.stockKey = stockKey;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
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
