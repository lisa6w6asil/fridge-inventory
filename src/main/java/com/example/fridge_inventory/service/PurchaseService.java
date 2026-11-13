package com.example.fridge_inventory.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fridge_inventory.entity.Purchase;
import com.example.fridge_inventory.form.PurchaseForm;
import com.example.fridge_inventory.repository.PurchaseRepository;

/**
 * 「買った」の一覧と登録を担当する。
 * 今は保存と取り出しだけだが、削除機能を足すときに
 * 「消すと在庫がマイナスになるなら止める」ルールをここに置く。
 */
@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    public PurchaseService(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    /** 買った記録を、購入日の新しい順で返す */
    @Transactional(readOnly = true)
    public List<Purchase> findAllNewestFirst() {
        return purchaseRepository.findAll(Sort.by(Sort.Direction.DESC, "purchasedDate", "id"));
    }

    @Transactional
    public void register(PurchaseForm form) {
        purchaseRepository.save(form.toEntity());
    }
}
