package com.example.fridge_inventory.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fridge_inventory.dto.StockItem;
import com.example.fridge_inventory.entity.Purchase;
import com.example.fridge_inventory.entity.Usage;
import com.example.fridge_inventory.form.UsageForm;
import com.example.fridge_inventory.repository.PurchaseRepository;
import com.example.fridge_inventory.repository.UsageRepository;

/**
 * 「使った」の登録・詳細・削除を担当する。
 * 在庫を確かめてから保存するので、在庫より多く使った記録は作られない。
 */
@Service
public class UsageService {

    private final UsageRepository usageRepository;
    private final PurchaseRepository purchaseRepository;
    private final StockService stockService;

    public UsageService(UsageRepository usageRepository, PurchaseRepository purchaseRepository,
                        StockService stockService) {
        this.usageRepository = usageRepository;
        this.purchaseRepository = purchaseRepository;
        this.stockService = stockService;
    }

    @Transactional
    public void register(UsageForm form) {
        StockItem stock = stockService.findStock(form.getStockKey())
                .orElseThrow(() -> new UsageRejectedException(
                        "stockKey", "notFound", "選んだ食材の在庫が見つかりません"));

        if (form.quantityInHundredths() > stock.remaining()) {
            throw new UsageRejectedException("quantity", "exceeds",
                    "在庫（残り " + stock.quantityDisplay() + " " + stock.unit() + "）より多くは使えません");
        }

        usageRepository.save(form.toEntity(stock));
    }

    /** 使った記録を1件探す（詳細画面用） */
    @Transactional(readOnly = true)
    public Optional<Usage> findById(Long id) {
        return usageRepository.findById(id);
    }

    /** 使った記録の単位。使った記録には単位がないので、同じ品目名の買った記録から借りる */
    @Transactional(readOnly = true)
    public String unitOf(String name) {
        return purchaseRepository.findFirstByName(name).map(Purchase::getUnit).orElse("");
    }

    /**
     * 使った記録を1件消す。
     * 消すと在庫は増える方向にしか動かないので、マイナスのチェックはいらない。
     */
    @Transactional
    public void delete(Long id) {
        usageRepository.deleteById(id);
    }
}
