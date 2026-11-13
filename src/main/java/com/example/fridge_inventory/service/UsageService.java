package com.example.fridge_inventory.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fridge_inventory.dto.StockItem;
import com.example.fridge_inventory.form.UsageForm;
import com.example.fridge_inventory.repository.UsageRepository;

/**
 * 「使った」の登録を担当する。
 * 在庫を確かめてから保存するので、在庫より多く使った記録は作られない。
 */
@Service
public class UsageService {

    private final UsageRepository usageRepository;
    private final StockService stockService;

    public UsageService(UsageRepository usageRepository, StockService stockService) {
        this.usageRepository = usageRepository;
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
}
