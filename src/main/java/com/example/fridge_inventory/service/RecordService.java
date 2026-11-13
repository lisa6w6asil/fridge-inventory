package com.example.fridge_inventory.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fridge_inventory.dto.RecordRow;
import com.example.fridge_inventory.entity.Purchase;
import com.example.fridge_inventory.entity.Usage;
import com.example.fridge_inventory.repository.PurchaseRepository;
import com.example.fridge_inventory.repository.UsageRepository;

/**
 * 「記録の編集」画面用に、買った記録と使った記録を1つの一覧にまとめる。
 */
@Service
public class RecordService {

    private final PurchaseRepository purchaseRepository;
    private final UsageRepository usageRepository;

    public RecordService(PurchaseRepository purchaseRepository, UsageRepository usageRepository) {
        this.purchaseRepository = purchaseRepository;
        this.usageRepository = usageRepository;
    }

    /** 日付の新しい順。同じ日なら「使った」を上に（あとから起きた出来事なので） */
    @Transactional(readOnly = true)
    public List<RecordRow> findAllNewestFirst() {
        // 使った記録には単位がないので、同じ品目名の買った記録から借りる。
        // 「同じ品目名は同じ単位」のルールがあるので、1つに決まる。
        Map<String, String> unitByName = new HashMap<>();
        List<RecordRow> rows = new ArrayList<>();

        for (Purchase p : purchaseRepository.findAll()) {
            unitByName.putIfAbsent(p.getName(), p.getUnit());
            rows.add(new RecordRow(RecordRow.Type.PURCHASE, p.getId(), p.getPurchasedDate(), p.getName(),
                    p.getQuantity(), p.getUnit(), p.getStorageType(), p.getExpiryDate(),
                    joinMemo(p.getCategory(), p.getNote())));
        }
        for (Usage u : usageRepository.findAll()) {
            rows.add(new RecordRow(RecordRow.Type.USAGE, u.getId(), u.getUsedDate(), u.getName(),
                    -u.getQuantity(), unitByName.getOrDefault(u.getName(), ""), u.getStorageType(), u.getExpiryDate(),
                    joinMemo(u.getPurpose(), u.getNote())));
        }

        rows.sort(Comparator.comparing(RecordRow::date).reversed()
                .thenComparing(RecordRow::type, Comparator.reverseOrder())
                .thenComparing(RecordRow::id, Comparator.reverseOrder()));
        return rows;
    }

    /** 2つのメモを「・」でつなぐ。空のものは飛ばす */
    private String joinMemo(String first, String second) {
        List<String> parts = new ArrayList<>();
        if (first != null && !first.isBlank()) {
            parts.add(first);
        }
        if (second != null && !second.isBlank()) {
            parts.add(second);
        }
        return String.join("・", parts);
    }
}
