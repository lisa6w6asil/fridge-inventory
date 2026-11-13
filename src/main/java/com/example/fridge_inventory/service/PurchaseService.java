package com.example.fridge_inventory.service;

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
 * 「買った」の一覧・詳細・登録・削除を担当する。
 * 削除するときは、在庫がマイナスにならないかを確かめてから消す。
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

    /** 買った記録を1件探す（詳細画面用） */
    @Transactional(readOnly = true)
    public Optional<Purchase> findById(Long id) {
        return purchaseRepository.findById(id);
    }

    @Transactional
    public void register(PurchaseForm form) {
        purchaseRepository.save(form.toEntity());
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
}
