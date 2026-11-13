package com.example.fridge_inventory.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fridge_inventory.dto.StockItem;
import com.example.fridge_inventory.entity.Purchase;
import com.example.fridge_inventory.form.PurchaseForm;
import com.example.fridge_inventory.repository.PurchaseRepository;

/**
 * 「買った」の一覧・詳細・登録・編集・削除を担当する。
 * ・同じ品目名は同じ単位でしか記録できない
 * ・在庫がマイナスになる編集・削除はできない
 */
@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final StockService stockService;

    public PurchaseService(PurchaseRepository purchaseRepository, StockService stockService) {
        this.purchaseRepository = purchaseRepository;
        this.stockService = stockService;
    }

    /** 買った記録を、購入日の新しい順で返す */
    @Transactional(readOnly = true)
    public List<Purchase> findAllNewestFirst() {
        return purchaseRepository.findAll(Sort.by(Sort.Direction.DESC, "purchasedDate", "id"));
    }

    /** 買った記録を1件探す（詳細画面・編集画面用） */
    @Transactional(readOnly = true)
    public Optional<Purchase> findById(Long id) {
        return purchaseRepository.findById(id);
    }

    @Transactional
    public void register(PurchaseForm form) {
        purchaseRepository.findFirstByNameAndUnitNot(form.getName(), form.getUnit())
                .ifPresent(other -> {
                    throw unitMismatch(form.getName(), other.getUnit());
                });
        purchaseRepository.save(form.toEntity());
    }

    /**
     * 買った記録を書き換える。
     * ・すでに使った分より少ない数量にはできない
     * ・使った記録がある在庫は、品目名・期限・保存方法を変えられない
     * ・同じ品目名の、ほかの記録と違う単位にはできない
     */
    @Transactional
    public void update(Long id, PurchaseForm form) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new PurchaseUpdateRejectedException(
                        "その記録は見つかりません（すでに削除されている可能性があります）"));

        String oldKey = StockItem.keyOf(purchase.getName(), purchase.getExpiryDate(), purchase.getStorageType());
        String newKey = StockItem.keyOf(form.getName(), form.getExpiryDate(), form.getStorageType());
        long remainingWithout = stockService.remainingOf(oldKey) - purchase.getQuantity();

        if (oldKey.equals(newKey)) {
            if (remainingWithout + form.quantityInHundredths() < 0) {
                long minimum = -remainingWithout;
                String minimumDisplay = BigDecimal.valueOf(minimum, 2).stripTrailingZeros().toPlainString();
                throw new PurchaseUpdateRejectedException(
                        "すでに使った分があるため、数量は " + minimumDisplay + " " + purchase.getUnit() + " より少なくできません");
            }
        } else if (remainingWithout < 0) {
            throw new PurchaseUpdateRejectedException(
                    "「" + purchase.getName() + "」はすでに使った記録があるため、品目名・期限・保存方法は変えられません");
        }

        purchaseRepository.findFirstByNameAndUnitNotAndIdNot(form.getName(), form.getUnit(), id)
                .ifPresent(other -> {
                    throw unitMismatch(form.getName(), other.getUnit());
                });

        form.applyTo(purchase);
        purchaseRepository.save(purchase);
    }

    /**
     * 買った記録を1件消す。
     * 消したあとの残りがマイナスになる（＝買っていない分まで使ったことになる）なら断る。
     */
    @Transactional
    public void delete(Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new PurchaseDeleteRejectedException(
                        "その記録は見つかりません（すでに削除されている可能性があります）"));

        String key = StockItem.keyOf(purchase.getName(), purchase.getExpiryDate(), purchase.getStorageType());
        long remainingAfter = stockService.remainingOf(key) - purchase.getQuantity();
        if (remainingAfter < 0) {
            throw new PurchaseDeleteRejectedException(
                    "「" + purchase.getName() + "」はすでに使った記録があるため、この買った記録は削除できません");
        }

        purchaseRepository.delete(purchase);
    }

    private UnitMismatchException unitMismatch(String name, String existingUnit) {
        return new UnitMismatchException(
                "「" + name + "」はこれまで「" + existingUnit + "」で記録しています。同じ単位で入力してください");
    }
}
