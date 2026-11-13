package com.example.fridge_inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.fridge_inventory.dto.PurchaseTotal;
import com.example.fridge_inventory.entity.Purchase;

/**
 * purchasesテーブルを操作する窓口。
 */
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    /**
     * 品目名＋期限＋保存方法 ごとに、買った数量を合計する（GROUP BY）。
     * 保存は1回ずつ別々の行のまま、ここで初めてまとめる。
     */
    @Query("""
            select new com.example.fridge_inventory.dto.PurchaseTotal(
                p.name, max(p.unit), p.expiryDate, p.storageType, sum(p.quantity))
            from Purchase p
            group by p.name, p.expiryDate, p.storageType
            """)
    List<PurchaseTotal> sumByGroup();
}
