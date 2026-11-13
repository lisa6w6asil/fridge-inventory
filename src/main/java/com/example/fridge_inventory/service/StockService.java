package com.example.fridge_inventory.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.fridge_inventory.dto.PurchaseTotal;
import com.example.fridge_inventory.dto.StockItem;
import com.example.fridge_inventory.dto.UsageTotal;
import com.example.fridge_inventory.repository.PurchaseRepository;
import com.example.fridge_inventory.repository.UsageRepository;

/**
 * 在庫を計算するクラス。
 * 在庫数 ＝ 買った合計 − 使った合計（品目名＋期限＋保存方法 ごと）。
 * 数量をどこにも上書き保存しないので、履歴が全部残り、計算もずれない。
 */
@Service
public class StockService {

    private final PurchaseRepository purchaseRepository;
    private final UsageRepository usageRepository;

    public StockService(PurchaseRepository purchaseRepository, UsageRepository usageRepository) {
        this.purchaseRepository = purchaseRepository;
        this.usageRepository = usageRepository;
    }

    /** 残りがある在庫を、保存方法 → 期限の近い順 → 名前順 で返す */
    public List<StockItem> findAllStocks() {
        // 1. 使った合計を「在庫の目印」ごとに表にしておく
        Map<String, Long> usedByKey = new HashMap<>();
        for (UsageTotal u : usageRepository.sumByGroup()) {
            usedByKey.put(StockItem.keyOf(u.name(), u.expiryDate(), u.storageType()), u.total());
        }

        // 2. 買った合計から使った合計を引いて、残りがあるものだけ在庫にする
        List<StockItem> stocks = new ArrayList<>();
        for (PurchaseTotal p : purchaseRepository.sumByGroup()) {
            long used = usedByKey.getOrDefault(StockItem.keyOf(p.name(), p.expiryDate(), p.storageType()), 0L);
            long remaining = p.total() - used;
            if (remaining > 0) {
                stocks.add(new StockItem(p.name(), p.unit(), p.expiryDate(), p.storageType(), remaining));
            }
        }

        // 3. 並べ替え（期限なしは最後）
        stocks.sort(Comparator.comparing(StockItem::storageType)
                .thenComparing(StockItem::expiryDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(StockItem::name));
        return stocks;
    }

    /**
     * 目印1つ分の残り（買った合計 − 使った合計）を返す。
     * findAllStocks と違い、0やマイナスでも捨てずにそのまま返す。
     */
    public long remainingOf(String key) {
        long bought = 0;
        for (PurchaseTotal p : purchaseRepository.sumByGroup()) {
            if (StockItem.keyOf(p.name(), p.expiryDate(), p.storageType()).equals(key)) {
                bought = p.total();
                break;
            }
        }
        long used = 0;
        for (UsageTotal u : usageRepository.sumByGroup()) {
            if (StockItem.keyOf(u.name(), u.expiryDate(), u.storageType()).equals(key)) {
                used = u.total();
                break;
            }
        }
        return bought - used;
    }

    /** プルダウンで選ばれた目印から在庫を探す */
    public Optional<StockItem> findStock(String key) {
        for (StockItem s : findAllStocks()) {
            if (s.key().equals(key)) {
                return Optional.of(s);
            }
        }
        return Optional.empty();
    }
}
